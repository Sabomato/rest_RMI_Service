package pd.g46.RestService.security;


import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import server.NotificationObservable;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;


public class AuthorizationFilter extends OncePerRequestFilter {

    private enum ValidationCode{
        NOT_EXISTENT,
        EXPIRED,
        OK
    }


    public static Map<String,Integer> tokens;
    public final static int LOGIN_TIME = 2 * 60 * 1000;
    public NotificationObservable notificationObservable;
    public AuthorizationFilter() throws RemoteException {

        notificationObservable = new NotificationObservable();
        notificationObservable.connectToRMI();
        HashMap<String,Integer> map =new HashMap<>();
        tokens = Collections.synchronizedMap(map);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        notificationObservable.notifyObservers();
        String token = httpServletRequest.getHeader("Authorization");

        ValidationCode code = isValid(token);

        switch (code){

            case OK -> {

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("USER"));

                UsernamePasswordAuthenticationToken uPAT =
                        new UsernamePasswordAuthenticationToken(token, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(uPAT);
            }
            case EXPIRED -> {
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);



    }

    public static String addToken(int userId){

        String key = encodeToken(userId + "_" + new Date().getTime());
        tokens.put(key, userId);

        return key;
    }

    public  static Integer getIdUser(String token){

        return tokens.get(token);
    }

    private ValidationCode isValid(String token) {

        if(!tokens.containsKey(token))
            return ValidationCode.NOT_EXISTENT;

        String authorization = decodeToken(token);

        long loginTime;

        loginTime = Long.parseLong(authorization.substring(2));

        boolean isValid = (System.currentTimeMillis() - loginTime) < LOGIN_TIME;

        if(!isValid){
            tokens.remove(token);
            return ValidationCode.EXPIRED;
        }

        return ValidationCode.OK;
    }


    public static String encodeToken(String authorization){
        return Base64.getEncoder().encodeToString(authorization.getBytes());
    }

    public static String decodeToken(String token){

        return new String(Base64.getDecoder().decode(token));
    }




}
