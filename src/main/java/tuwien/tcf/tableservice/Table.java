/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tuwien.tcf.tableservice;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tuwien.tcf.model.HttpContentResponse;
import tuwien.tcf.model.TableModel;
import tuwien.tcf.process.Score;
import tuwien.tcf.process.UserTables;

/**
 *
 * @author amin
 */
@RestController
public class Table {
    
    @Autowired
    private UserTables userTables;
    
    @Autowired
    private Score score;
    
    
    @DeleteMapping("/deleteUserTable")
    public HttpEntity<?> deleteTables() {
        userTables.deleteAll();
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.ContentApproved);
        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }

    /**
     *
     * @param tableJson
     * @return
     */
    @PostMapping("/importtable")
    public HttpEntity<?> importTable(@RequestBody String tableJson) {

        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(tableJson);
            JsonObject jsobj = jsonElement.getAsJsonObject();
            
            TableModel table = new TableModel();
            table.setHeader(jsobj.get("header").getAsString());
            table.setFileID(jsobj.get("fileid").getAsString());
            table.setTableCounter(Integer.parseInt(jsobj.get("tablecounter").toString()));
            table.setRows(jsobj);

            userTables.addUserTable(table);
        } catch (Exception jse) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, jse.getMessage());
            HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.ContentNotApproved);
            return new ResponseEntity<>(hcr, HttpStatus.OK);
        } 
        HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.ContentApproved);
        return new ResponseEntity(hcr, HttpStatus.ACCEPTED);
    }
    
    @GetMapping("/calculateScore")
    public HttpEntity<?> calculateScore() {
        Double retValue = Double.valueOf(0);
        try {
            retValue = score.calculateScoring();
        }catch (Exception jse) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, jse.getMessage());
            HttpContentResponse hcr = new HttpContentResponse(HttpContentResponse.ContentNotApproved);
            return new ResponseEntity<>(hcr, HttpStatus.OK);
        } 
        HttpContentResponse hcr = new HttpContentResponse(String.format(HttpContentResponse.ScoreValue, retValue));
        
        return new ResponseEntity(hcr, HttpStatus.OK);
    }

}
