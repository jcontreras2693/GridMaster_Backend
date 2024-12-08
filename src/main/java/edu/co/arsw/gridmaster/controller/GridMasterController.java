package edu.co.arsw.gridmaster.controller;

import edu.co.arsw.gridmaster.model.Player;
import edu.co.arsw.gridmaster.model.Position;
import edu.co.arsw.gridmaster.model.exceptions.GameException;
import edu.co.arsw.gridmaster.model.exceptions.GameNotFoundException;
import edu.co.arsw.gridmaster.model.exceptions.GridMasterException;
import edu.co.arsw.gridmaster.model.exceptions.PlayerSaveException;
import edu.co.arsw.gridmaster.service.GridMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@CrossOrigin(origins = "https://gentle-coast-03f74f10f.5.azurestaticapps.net/")
// @CrossOrigin(origins = "http://localhost:5500/")
@RequestMapping
public class GridMasterController {

    GridMasterService gridMasterService;

    @Autowired
    public GridMasterController(GridMasterService gridMasterService){
        this.gridMasterService = gridMasterService;
    }

    // GET Requests

    @GetMapping
    public ResponseEntity<?> basePage(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/games")
    public ResponseEntity<?> getAllGames(){
        try{
            return new ResponseEntity<>(gridMasterService.getAllGames(), HttpStatus.ACCEPTED);
        }
        catch(GridMasterException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/games/{code}")
    public ResponseEntity<?> getGameByCode(@PathVariable Integer code){
        try {
            return new ResponseEntity<>(gridMasterService.getGameByCode(code), HttpStatus.ACCEPTED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/games/{code}/score")
    public ResponseEntity<?> getScoreboardByCode(@PathVariable Integer code){
        try {
            return new ResponseEntity<>(gridMasterService.getScoreboard(code), HttpStatus.ACCEPTED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/games/{code}/time")
    public ResponseEntity<?> getTimeByCode(@PathVariable Integer code){
        try {
            return new ResponseEntity<>(gridMasterService.getTime(code), HttpStatus.ACCEPTED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/games/{code}/players")
    public ResponseEntity<?> getAllPlayers(@PathVariable Integer code){
        try {
            return new ResponseEntity<>(gridMasterService.getPlayers(code), HttpStatus.ACCEPTED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/games/{code}/players/{name}")
    public ResponseEntity<?> getPlayerByName(@PathVariable Integer code,
                                             @PathVariable String name){
        try {
            return new ResponseEntity<>(gridMasterService.getPlayerByName(code, name), HttpStatus.ACCEPTED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // POST Requests

    @PostMapping(value = "/games")
    public ResponseEntity<?> createGame() {
        try {
            return new ResponseEntity<>(gridMasterService.createGridMaster(), HttpStatus.CREATED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // PUT REQUESTS

    @PutMapping(value = "/games/{code}")
    public ResponseEntity<?> updateGame(@PathVariable Integer code,
                                        @RequestBody HashMap<String, Integer> settings){
        try {
            gridMasterService.updateGame(code, settings);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/games/{code}/started")
    public ResponseEntity<?> startGame(@PathVariable Integer code){
        try {
            gridMasterService.startGame(code);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/games/{code}/players")
    public ResponseEntity<?> addPlayer(@PathVariable Integer code,
                                     @RequestBody Player player){
        try {
            gridMasterService.addPlayer(code, player.getName());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (GameNotFoundException e) {
            return new ResponseEntity<>("Game not found.", HttpStatus.NOT_FOUND);
        } catch (GameException e) {
            return new ResponseEntity<>("Room is full.", HttpStatus.CONFLICT);
        } catch (PlayerSaveException e) {
            return new ResponseEntity<>("Name is in use. Please choose another.", HttpStatus.FORBIDDEN);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/games/{code}/players/{name}")
    public ResponseEntity<?> movePlayer(@PathVariable Integer code,
                                        @PathVariable String name,
                                        @RequestBody Position newPosition){
        try {
            gridMasterService.move(code, name, newPosition);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/games/{code}/finished")
    public ResponseEntity<?> endGame(@PathVariable Integer code){
        try {
            gridMasterService.endGame(code);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // DELETE Requests

    @DeleteMapping(value = "{code}")
    public ResponseEntity<?> deleteGame(@PathVariable Integer code){
        try {
            gridMasterService.deleteGridMaster(code);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (GridMasterException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/games/{code}/players/{name}")
    public ResponseEntity<?> deletePlayer(@PathVariable Integer code, @PathVariable String name){
        try {
            gridMasterService.deletePlayer(code, name);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch(GridMasterException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
