package pd.g46.RestService.controllers;



import pd.g46.RestService.data.structures.contact.ContactDB;
import pd.g46.RestService.data.structures.group.Group;
import pd.g46.RestService.data.structures.txtMsg.TxtMsgGroup;
import pd.g46.RestService.data.structures.txtMsg.TxtMsgUser;
import pd.g46.RestService.data.structures.user.UserDB;
import pd.g46.RestService.data.structures.user.UserPublic;
import pd.g46.RestService.data.database.DBManager;
import pd.g46.RestService.data.database.StatusMsg;
import pd.g46.RestService.data.database.queryable.Deletable;
import pd.g46.RestService.data.database.queryable.Updatable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pd.g46.RestService.security.AuthorizationFilter;

import java.util.ArrayList;

import static pd.g46.RestService.data.database.StatusMsg.*;
import static pd.g46.RestService.controllers.MsgServiceController.MSG_SERVICE;

@RestController
@RequestMapping(MSG_SERVICE)
public class MsgServiceController {

    public static final String MSG_SERVICE = "msg-service";
    public static final String CONTACTS = "contacts";
    public static final String GROUPS = "groups";
    public static final String MESSAGES = "messages";
    public static final String LOGIN = "login";
    public static final String PROFILE = "profile";
    /*
        ▪ Autenticar-se;
        ▪ Alterar o seu nome;
        ▪ Obter a lista dos seus contactos;
        ▪ Eliminar um contacto;
        ▪ Obter a listar dos grupos a que pertence;
        ▪ Obter a lista de mensagens que recebeu de um determinado contacto (não incluir
        notificações de disponibilização de ficheiros);
        ▪ Obter a lista de mensagens que recebeu de um determinado grupo (não incluir
        notificações de disponibilização de ficheiros).
    */
    //Exerciccio da aula

    @PostMapping(LOGIN)
    public ResponseEntity login(@RequestParam(name="username") String username, @RequestParam(name="password") String password)
    {
        UserDB[] user = new UserDB[]{new UserDB(password,username)};

         StatusMsg status =  DBManager.loginSimple(user);

         switch (status){

             case SUCESS -> {
                 return ResponseEntity.ok(AuthorizationFilter.addToken(user[0].getIdUser()));
             }
             case SQL_ERROR -> {
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(StatusMsg.SQL_ERROR.toString());
             }
             case UNREGISTERED_CLIENT -> {
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You need to be registered!");
             }
             case WRONG_PASSWORD -> {
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
             }
             default -> {
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There was an error on the server");
             }
         }

    }

    @PostMapping(PROFILE)
    public ResponseEntity updateName(@RequestParam(name="newName") String newName)
    {
        String token = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Integer id = AuthorizationFilter.getIdUser(token);

        Updatable user = new UserDB(id,newName);

        boolean result =  DBManager.updateRow(user);

        return !result ? ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update the name!")
                :
        ResponseEntity.ok("Name updated with success!");


    }

    @GetMapping("")
    public String MsgService(){


        return "Rest Service for the subject of Distributted Programming";


    }

    //adicion
    @GetMapping(CONTACTS)
    public ResponseEntity getContacts(){

        String token = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Integer id = AuthorizationFilter.getIdUser(token);

        if( id == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");


        ArrayList<UserPublic> contacts = new ArrayList<>();

        if(DBManager.getAllContacts(id,contacts))
            return ResponseEntity.ok(contacts);

        //503 - service  unavailable since probably the query fails because the database is not up
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to query the database!");

    }


    @DeleteMapping(CONTACTS + "/{id}")
    public ResponseEntity deleteContact(@PathVariable("id")Integer idToRemove){

        String token = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Integer id = AuthorizationFilter.getIdUser(token);

        if( id == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

        //Vai à db, buscar os contactos e returna

        Deletable deletable = new ContactDB(id,  idToRemove);

        if(DBManager.deleteRow(deletable))
            return ResponseEntity.ok("Contact deleted with success!");

        //503 - service  unavailable since probably the query fails because the database is not up
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the contact!");
    }

    @GetMapping(GROUPS)
    public ResponseEntity getGroups(){

        String token = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Integer userId = AuthorizationFilter.getIdUser(token);

        if( userId == null )
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

        ArrayList<Group> groups = new ArrayList<>();

        if( DBManager.getAllGroups(userId, groups) )
            return ResponseEntity.ok(groups);

        return ResponseEntity.internalServerError().body("There was an error on the server");
    }


    @GetMapping(GROUPS + "/" +"{id}" + "/" + MESSAGES)
    public ResponseEntity getGroupMessages(@PathVariable("id")Integer id){

        String token = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Integer userId = AuthorizationFilter.getIdUser(token);

        if( userId == null )
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

        ArrayList<TxtMsgGroup> txtMsgs = new ArrayList<>();

        if( DBManager.getMsgsGroupAfterId(id, txtMsgs, 0, 0) == SUCESS)
            return ResponseEntity.ok(txtMsgs);

        return ResponseEntity.internalServerError().body("There was an error on the server");
    }

    @GetMapping(CONTACTS + "/" +"{id}" + "/" + MESSAGES)
    public ResponseEntity getContactMessages(@PathVariable("id")Integer id ){

        String token = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Integer userId = AuthorizationFilter.getIdUser(token);

        if( userId == null )
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

        ArrayList<TxtMsgUser> txtMsgs = new ArrayList<>();

        if( DBManager.getMsgsContactAfterId(userId, id, txtMsgs, 0, 0) == SUCESS)
            return ResponseEntity.ok(txtMsgs);

        return ResponseEntity.internalServerError().body("There was an error on the server");
    }



}
