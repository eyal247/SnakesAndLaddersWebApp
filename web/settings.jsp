<%-- 
    Document   : settings
    Created on : 15/09/2014, 15:48:00
    Author     : EyalEngel
--%>

<%@page import="utils.SessionUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <title>Snakes And Ladders</title>
        <meta charset="UTF-8">
        <script src="scripts/jquery-2.1.1.min.js"></script>
        <script src="scripts/settingsScript.js"></script>
        <script src="scripts/mainMenuScript.js"></script>
        <link rel="stylesheet" href="css/bootstrap.css">
        <link rel="stylesheet" href="css/settingsCss.css">
        <link rel="stylesheet" href="css/musicButtonCss.css">
        <link rel="stylesheet" href="css/buttonsCss.css">
        <link rel="SHORTCUT ICON" href="images/logo.png">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <audio loop autoplay id="game-audio">
            <source src="./audio/theme.mp3" type="audio/mpeg">
        </audio>
        <br>
        <div class="rss">
            <img id="audio-image" src="./images/audio_image.png" alt="Audio" width="40" height="40" style="margin-top:-5px"/>
            <input class="audio-button-input" type="checkbox" id="buttonThree" onchange="playOrPauseMusic()" />
            <label class="audio-button-label" for="buttonThree"> <!-- class="attention" -->
                <i></i>
            </label>
        </div><!-- /rss -->
        <form method="POST" action="createGame" id="settings-form">
            <div>
                <label class="settings-labels">Game Name: </label>
                <input class="name-text-box" type="text" name="gameName"/>
            </div>
            <div>
                <label class="settings-labels">Number Of Players: </label>
                <!--                <span id="numOfPlayersVal"></span>-->
                <select id="num-of-players" name="numOfPlayers" onchange="setNumOfComputers(this.value)">
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                </select>
            </div>
            <br>
            <div class="computers" id="num-of-computers-div">
                <label class="settings-labels">Number Of Computers: </label>
                <select id="num-of-computers" name="numOfComputers" onchange="createComputersTextBoxes(this.value)">
                    <option value="0">0</option>
                    <option value="1">1</option>
                </select>
            </div>
            <div class="computers" id="computers-text-boxes">
            </div>
            <div>
                <label class="settings-labels">Board Size: </label>
                <select id="board-size" name="boardSize" onchange="setMaxNumOfSnl(this.value)">
                    <option value="5">5x5</option>
                    <option value="6">6x6</option>
                    <option value="7">7x7</option>
                    <option value="8">8x8</option>
                </select>
            </div>
            <div>
                <label class="settings-labels">Number Of Snakes/Ladders: </label>
                <select id="num-of-snl" name="numOfSnl">
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                </select>
            </div>
            <div>
                <label class="settings-labels">Number Of Tokens To Win: </label>
                <input type="radio" name="numOfTokensToWin" value="1" checked="checked"/>
                <img src ="images/1soldier.png" alt="one token" class="soldier-image">
                <input type="radio" name="numOfTokensToWin" value="2"/>
                <img src ="images/2soldiers.png" alt="two tokens" class="soldier-image">
                <input type="radio" name="numOfTokensToWin" value="3"/>
                <img src ="images/3soldiers.png" alt="three tokens" class="soldier-image">
                <input type="radio" name="numOfTokensToWin" value="4"/>
                <img src ="images/4soldiers.png" alt="four tokens" class="soldier-image">
            </div>
        </form>
        <div class="bottom-line">
            <button class="button wood" id="back-to-menu-button" value="Back To Menu" onclick="onClickBackToMenu()">Back To Menu</button>
            <button class="button wood" id="continue-button" value="Continue" onclick="onContinueClickedBeforeValidation()">Continue</button>    
        </div>
        <div id="error-message-container" class="bottom-line">
        </div>
    </body>
</html>
