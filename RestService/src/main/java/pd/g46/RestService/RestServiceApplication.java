package pd.g46.RestService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pd.g46.RestService.controllers.MsgServiceController;

import pd.g46.RestService.security.AuthorizationFilter;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;



@SpringBootApplication
public class RestServiceApplication {

	public static void main(String[] args) {



		try {

			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

			System.out.println("Registry launched!");

		}catch(RemoteException e){
			System.out.println("Registry is already in execution!");
		}

		SpringApplication.run(RestServiceApplication.class, args);




	}

	@EnableWebSecurity
	@Configuration
	 class WebSecurityConfig extends WebSecurityConfigurerAdapter
	{
		@Override
		protected void configure(HttpSecurity http) throws Exception
		{
			http.csrf().disable()
					.addFilterAfter(new AuthorizationFilter(),
							UsernamePasswordAuthenticationFilter.class)
					.authorizeRequests()
					.antMatchers(HttpMethod.POST, "/"+ MsgServiceController.MSG_SERVICE +"/" + MsgServiceController.LOGIN).permitAll()
					.anyRequest().authenticated().and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		}
	}


}
