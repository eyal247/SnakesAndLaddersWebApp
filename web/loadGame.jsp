<%--
    Document   : loadGame
    Created on : 05/10/2014, 13:15:46
    Author     : EyalEngel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Load Game</title>
        <meta charset="UTF-8">
        <script src="scripts/jquery-2.1.1.min.js"></script>
        <script src="scripts/loadGameScript.js"></script>
        <script src="scripts/mainMenuScript.js"></script>
        <link rel="stylesheet" href="css/mainMenuCss.css">
        <link rel="stylesheet" href="css/buttonsCss.css">
        <link rel="stylesheet" href="css/loadGameCss.css">
        <link rel="stylesheet" href="css/musicButtonCss.css">
        <link rel="SHORTCUT ICON" href="images/logo.png">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <audio loop autoplay id="game-audio">
            <source src="./audio/theme.mp3" type="audio/mpeg">
        </audio>
        <br>
        <div class="rss">
            <img id="audio-image" src="./images/audio_image.png" alt="Audio"/>
            <input class="audio-button-input" type="checkbox" id="buttonThree" onchange="playOrPauseMusic()" />
            <label class="audio-button-label" for="buttonThree"> <!-- class="attention" -->
                <i></i>
            </label>
        </div><!-- /rss -->
        <div class="menu-buttons"> 
            <button class="button wood" onclick="openFileChooserWindow()">Choose A File</button><br><br>
            <button class="button wood" onclick="goBackToMenu()">Back To Menu</button><br>
        </div>
        <form action="loadGame" id="load-game-form" enctype="multipart/form-data" method="POST">    
            <input type="file" id="load-file" name="loadedFile" onchange="this.form.submit()" accept="text/xml">
        </form>
        <% Object errorMessage = request.getAttribute("errorMsg");%>
        <% if (errorMessage != null) {%>
        <span id="error-message"><%=errorMessage%></span>
        <%}%>
    </body>
</html>

