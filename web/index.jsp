<%-- 
    Document   : index
    Created on : 07/09/2014, 13:45:27
    Author     : EyalEngel
--%>
<%@page import="utils.SessionUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <title>Snakes And Ladders</title>
        <script src="scripts/jquery-2.1.1.min.js"></script>
        <script src="scripts/loadGameScript.js"></script>
        <script src="scripts/mainMenuScript.js"></script>
        <link rel="stylesheet" href="css/mainMenuCss.css">
        <link rel="stylesheet" href="css/buttonsCss.css">
        <link rel="stylesheet" href="css/musicButtonCss.css">
        <link rel="shortcut icon" href="images/logo.png">
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <audio loop autoplay id="game-audio">
            <source src="./audio/theme.mp3" type="audio/mpeg">
        </audio>
        <br>
        <div class="rss" id="menu-rss">
            <img id="audio-image" src="./images/audio_image.png" alt="Audio" alt="Audio"/>
            <input class="audio-button-input" type="checkbox" id="buttonThree" onchange="playOrPauseMusic()" />
            <label class="audio-button-label" for="buttonThree"> <!-- class="attention" -->
                <i></i>
            </label>
        </div><!-- /rss -->
        <div class="menu-buttons">
            <form method="POST" action="startNewGame">    
                <button class="button wood" name="startNewGame">Start New Game</button>
            </form>
            <br>
            <form method="POST" action="joinGame">    
                <button class="button wood" name="joinGame">Join A Game</button>
            </form>
            <br>
            <button class="button wood" onclick="goToLoadGameScreen()">Load Game From XML</button>
        </div>
    </body>
</html>

