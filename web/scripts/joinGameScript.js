/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var refreshRate = 1000;
var selectedGameNameGlobal;

$(function() { 
    $("#joining-player-text-box").keyup(saveCurrentInput);
    disableButton("join-game-button");
    ajaxUpdateGamesList();
    setInterval(ajaxUpdateGamesList, 2000);
});

function ajaxUpdateGamesList() {
    jQuery.ajax({
        dataType: 'json',
        url: "waitingGamesList",
        timeout: 2000,
        error: function() {
            console.error("failed to submit");
        },
        success: function(data) {
            displayGamesList(data.waitingGamesNames);
            setSelectedGame(data.waitingGamesNames);
        }
    });
}

function displayGamesList(waitingGamesList) {
    var radioButton;

    removeTagsFromContainer('#radio-buttons-border', 'label');
    removeTagsFromContainer('#radio-buttons-border', 'input');
    removeTagsFromContainer('#radio-buttons-border', 'br');

    if (waitingGamesList.length === 0) {
        var noGamesLabel = "<label class=radio-button-label style=text-align: center>There are no waiting games</label>";
        $("#radio-buttons-border").append(noGamesLabel);
    } else {
        for (var i = 0; i < waitingGamesList.length; i++) {
            var gameNumberInList = i + 1;
            radioButton = "<label class=radio-button-label><input id=game" + gameNumberInList + "-radio-button class=games-radio-buttons type=radio name=gameName value='" + waitingGamesList[i] + "' onclick=checkIfGameIsLoaded(this)>" + waitingGamesList[i] + "</label><br>";
            $("#radio-buttons-border").append(radioButton);
        }
    }

}

function saveCurrentInput() {
    selectedUserNameGlobal = $("#joining-player-text-box").val();
}

function setSelectedGame(waitingGamesList) {
    for (var i = 0; i < waitingGamesList.length; i++) {
        var gameNumberInList = i + 1;
        if (selectedGameNameGlobal === waitingGamesList[i]) {
            $("#game" + gameNumberInList + "-radio-button").attr("checked", "checked");
            $("#joining-player-text-box").val(selectedUserNameGlobal);
            var playersNames = document.getElementsByClassName("players-names");
            for (var j = 0; j < playersNames.length; j++) {
                var num = j + 1;
                if ($("#player" + num + "-name").val() === selectedUserNameGlobal)
                    $("#player" + num + "-name").attr("selected");
            }
        }
    }
}

function checkIfGameIsLoaded(selectedButton) {
    $("#error-message-container").html("");
    activateButton("join-game-button");
    var selectedGameName = $(selectedButton).val();
    selectedGameNameGlobal = selectedGameName;
    ajaxIsGameLoaded(selectedGameName);
}

function ajaxIsGameLoaded(selectedGameName) {
    jQuery.ajax({
        url: "isGameLoaded?gameName=" + selectedGameName,
        dataType: 'json',
        timeout: 2000,
        error: function() {
            console.error("failed to submit");
        },
        success: function(data) {
            if (data.gameLoaded === true) {
                disableUserNameTextBox();
                displayPlayersNames(data.waitingPlayersNames);
            } else {
                hidePlayersNames();
                enableUserNameTextBox();
            }
            selectedUserNameGlobal = "";
        }
    });
}

function displayPlayersNames(waitingPlayersNames) {
    var nameOption;
    var header;

    hidePlayersNames();
    document.getElementById("loaded-players-names").style.visibility = "visible";
    header = "<option selected disabled>Choose a username</option>";
    $("#loaded-players-names").append(header);

    for (var i = 0; i < waitingPlayersNames.length; i++) {
        var playerNumberInList = i + 1;
        nameOption = "<option class=players-names id=player" + playerNumberInList + "-name name=playerName value=" + waitingPlayersNames[i] + ">" + waitingPlayersNames[i];
        $("#loaded-players-names").append(nameOption);
    }
}

function updateUserNameTextBox() {
    var selectedPlayerName = document.getElementById("loaded-players-names").value;
    $("#joining-player-text-box").val(selectedPlayerName);
    $("#error-message-container").html("");
    selectedUserNameGlobal = selectedPlayerName;
}

function disableUserNameTextBox() {
    $("#joining-player-text-box").attr("disabled", "disabled");
    $("#joining-player-text-box").val("");
}

function hidePlayersNames() {
    removeTagsFromContainer('#loaded-players-names', 'option');
    document.getElementById("loaded-players-names").style.visibility = "hidden";
}

function enableUserNameTextBox() {
    $("#joining-player-text-box").removeAttr("disabled");
    $("#joining-player-text-box").val("");
}

function removeTagsFromContainer(idStr, tagNameStr) {
    $(idStr)
            .find(tagNameStr)
            .remove()
            .end();
}

function onJoinGameClickedBeforeValidation() {
    $("#joining-player-text-box").removeAttr("disabled");

    jQuery.ajax({
        data: $("#join-game-form").serialize(),
        dataType: 'json',
        url: "validateJoiningPlayer",
        timeout: 2000,
        error: function() {
            console.error("failed to submit");
        },
        success: function(data) {
            if (data.errorMsg === "valid") {
                $("#join-game-form").submit();
            }
            else {
                $("#error-message-container").html("");
                $("#error-message-container").append('<span id=error-message>' + data.errorMsg + '</span>');
            }
        }
    });
}

function onClickBackToMenu() {
    window.location = "index.html";
}

