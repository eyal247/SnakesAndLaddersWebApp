/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 **/

var NUM_OF_RANDOM_DICE_RESULTS = 24;
var DELAY_BETWEEN_DICE_RESULTS = 100;
var REFRESH_RATE = 1000;
var TIMEOUT_RATE = 2000;
var boardSizeGlobal;

$(function() {
    appendUtilsScriptFile();
    ajaxGetGameName();
});

function appendUtilsScriptFile() {
    var importedFile = document.createElement('script');

    importedFile.src = 'scripts/utilsScript.js';
    document.head.appendChild(importedFile);
}

function ajaxGetGameName() {
    jQuery.ajax({
        url: "getGameName",
        timeout: TIMEOUT_RATE,
        error: function() {
            console.error("Failed to submit");
        },
        success: function(gameName) {
            $("#game-name").text(gameName);
            $("#game-name-input").val(gameName);
            var gameName = $("#game-name").text();
            ajaxGetParameters(gameName);
        }
    });
}

function ajaxGetParameters(gameName) {
    jQuery.ajax({
        url: "gameParameters?gameName=" + gameName,
        dataType: 'json',
        timeout: TIMEOUT_RATE,
        error: function() {
            console.error("Failed to submit");
        },
        success: function(data) {
            var boardSize = data.boardDimension * data.boardDimension;
            boardSizeGlobal = boardSize;
            drawBoard(data.initialBoardStatus, data.boardDimension, data.snakes, data.ladders);
            setGameScreenLabels(data.playersNames, data.playersTypes, data.currPlayerName,
                    data.numOfTokensToWin, data.numOfComputers);
            setVisibleTokens(boardSize, data.initialBoardStatus, data.numOfPlayers);
            if (data.gameActive === false) {
                triggerAjaxCheckIfGameIsActive();
                displayWaitingForPlayersMsg(data.numOfPlayers, data.playersNames.length, data.gameLoaded, data.numOfJoinedPlayers);
                disableGameScreenElements(boardSize);
            }
            else {
                if (data.isCurrentPlayerComputer === true)
                    ajaxPlayComputerTurn(gameName);
                triggerAjaxRefreshScreen();
            }

        }
    });
}

function ajaxCheckIfGameIsActive(gameName) {
    $.ajax({
        url: "gameStatus?gameName=" + gameName,
        dataType: 'json',
        success: function(data) {
            if (data.isGameActive === true) {
                activateGameScreen();
                if (data.isCurrentPlayerComputer === true) {
                    ajaxPlayComputerTurn(gameName);
                }
                triggerAjaxRefreshScreen();
            }
            else {
                triggerAjaxCheckIfGameIsActive();
                displayWaitingForPlayersMsg(data.totalNumOfPlayers, data.playersNames.length, data.gameLoaded, data.numOfJoinedPlayers);
            }
            setPlayersNamesLabels(data.playersNames, data.playersTypes, data.numOfComputers);
        },
        error: function(error) {
        }
    });
}


function triggerAjaxCheckIfGameIsActive() {
    var gameName = $("#game-name").text();
    setTimeout(ajaxCheckIfGameIsActive(gameName), REFRESH_RATE);
}

function setGameScreenLabels(playersNames, playersTypes, currPlayerName, numOfTokensToWin, numOfComputers) {
    setPlayersNamesLabels(playersNames, playersTypes, numOfComputers);
    setCurrPlayerLabel(currPlayerName);
    setNumOfTokensToWinLabel(numOfTokensToWin);
}

function setCurrPlayerLabel(currPlayerName) {
    $("#curr-player-label").append(currPlayerName);
}

function setPlayersNamesLabels(playersNames, playersTypes, numOfComputers) {
    var currPlayerLabel;
    var playersLabels = "";
    for (var i = 1; i <= playersNames.length; i++) {
        currPlayerLabel = setPlayerLabel(i, playersNames, playersTypes);
        playersLabels += currPlayerLabel;
    }

    removeHtmlTagsById('#players-names-labels', 'li');
    $("#players-names-labels").append(playersLabels);
    setLabelMarginLeft(numOfComputers);
}

function setPlayerLabel(playerNumber, playersNames, playersTypes) {
    var currPlayerLabel = "<li class=name-label id=player" + playerNumber + "-label>";
    if (playersTypes[playerNumber - 1] === "COMPUTER") {
        currPlayerLabel += "<img id=computer-img src=images/Computer.png>";
    }

    currPlayerLabel += "Player " + playerNumber + ": " + playersNames[playerNumber - 1] + "</li>";

    return currPlayerLabel;
}

function setLabelMarginLeft(numOfComputers) {
    if (numOfComputers === 0) {
        $('.name-label').css("margin-left", "0px");
    }
}

function removeHtmlTagsById(idStr, tagNameStr) {
    $(idStr)
            .find(tagNameStr)
            .remove()
            .end();
}

function setNumOfTokensToWinLabel(numOfTokensToWin) {
    var numOfTokensLabel = "Number of tokens to win: " + numOfTokensToWin;
    $("#num-of-tokens-label").append(numOfTokensLabel);
}

function displayWaitingForPlayersMsg(totalNumOfPlayers, numOfActivePlayers, isGameLoaded, numOfJoinedPlayers) {
    var playersToJoin;
    if (isGameLoaded === false) {
        playersToJoin = totalNumOfPlayers - numOfActivePlayers;
    } else {
        playersToJoin = totalNumOfPlayers - numOfJoinedPlayers;
    }

    var waitingForPlayersStr = "<label class=waiting-msg id=game-msg-label> Waiting For " + playersToJoin + " Player/s To Join </label>";
    displayGameMessage(waitingForPlayersStr);
}

function displayGameMessage(msgStr) {
    removeHtmlTagsById("#game-msg", "label");
    $("#game-msg").append(msgStr);
    setOpacity("#game-screen-elements-container", 0.5);
    setSnlOpacity("0.5");
}

function setSnlOpacity(opacityValue) {
    var snlImages = document.getElementsByClassName('snl');
    for (var i = 0; i < snlImages.length; i++) {
        document.getElementsByClassName('snl')[i].style.opacity = opacityValue;
    }
}

function activateGameScreen() {
    setOpacity("#game-screen-elements-container", 1.0);
    $("#game-msg-label").remove();
    activateButton("roll-dice-button");
    activateButton("quit-button");
    setSnlOpacity("1.0");
}

function triggerAjaxRefreshScreen() {
    var gameName = $("#game-name").text();
    setTimeout(ajaxRefreshScreen(gameName), REFRESH_RATE);
}

function ajaxRefreshScreen(gameName) {
    $.ajax({
        url: 'refreshGameScreen?gameName=' + gameName,
        dataType: 'json',
        success: function(data) {
            var boardSize = data.inWhichCellsCurrPlayerHasTokens.length;
            var endGameMsg;
            var boardSize = data.boardDimension * data.boardDimension;
            updateScreen(data);
            if (data.gameStatus !== "GAME_STILL_ON") {
                setMessageColor(data.currMoveInfo.playerNumber);
                endGameMsg = setEndGameMsg(data);
                playGameSounds("applause.wav");
                displayGameMessage(endGameMsg);
                displayBackToMenuButton();
                disableGameScreenElements(boardSize);
            } else {
                if (notMyTurn(data)) {
                    disableBoardElements(boardSize);
                    triggerAjaxRefreshScreen();
                } else {
                    activateDiceElements();
                    startTimer();
                }
            }
        },
        error: function(error) {
        }
    });
}

function setMessageColor(currPlayerNumber) {
    var messageColor = getMessageColor(currPlayerNumber);
    $("#game-msg").css("color", messageColor);

    return messageColor;
}

function setEndGameMsg(data) {
    var endGameMsg;

    if (data.gameStatus === "PLAYER_WON") {
        endGameMsg = "<label class=end-game-msg id=game-msg-label>" +
                data.currPlayerName + " Won The Game!</label>";
    } else if (data.gameStatus === "ONE_HUMAN_NO_COMPUTERS") {
        endGameMsg = "<label class=end-game-msg id=game-msg-label>All Players Have Left The Game..." +
                "<br>" + "Therefore " + data.currPlayerName + " Is The Winner!</label>";
    }

    return endGameMsg;
}

function activateDiceElements() {
    activateButton("roll-dice-button");
    $("#dice-result").css("color", "#222");
}

function playGameSounds(fileName) {
    var audioTag = "<audio id=" + fileName + "><source src=audio/" + fileName + " type=audio/mpeg ></audio>";
    document.body.innerHTML += audioTag;

    var audio = document.getElementById(fileName);
    audio.play();
}

function getMessageColor(playerNumber) {
    var messageColor;
    switch (playerNumber) {
        case 1:
            messageColor = "orangered";
            break;
        case 2:
            messageColor = "lime";
            break;
        case 3:
            messageColor = "aqua";
            break;
        case 4:
            messageColor = "yellow";
            break;
        default:
            messageColor = "darkblue";
            break;
    }

    return messageColor;
}

function startTimer() {
    triggerGetTurnTimer();
}

function triggerGetTurnTimer() {
    var gameName = $("#game-name").text();
    setTimeout(ajaxGetTurnTimer(gameName), REFRESH_RATE);
}

function ajaxGetTurnTimer(gameName) {
    $.ajax({
        url: 'turnTimer?gameName=' + gameName,
        dataType: 'json',
        success: function(timerData) {
            displayTimerSeconds(timerData);
        },
        error: function(error) {
        }
    });
}

function displayTimerSeconds(timerData) {
    if (timerData.instanceIntialized === true) {
        if (timerData.turnPlayed === false) {
            document.getElementById("seconds-label").innerHTML = timerData.secondsLeft;
            if (timerData.secondsLeft < parseInt("10")) {
                document.getElementById("seconds-label").style.marginLeft = "30px";
            }
            if (timerData.secondsLeft <= parseInt("0")) {
                $("#quit-button").click();
            }
            else {
                triggerGetTurnTimer();
            }
        } else {
            document.getElementById("seconds-label").innerHTML = timerData.secondsToPlayTurn;
            document.getElementById("seconds-label").style.marginLeft = "23px";
        }
    }
}

function notMyTurn(data) {
    return (data.inWhichCellsCurrPlayerHasTokens.length === 0);
}


function ajaxAfterGameEnded(gameName) {
    $.ajax({
        url: "endGame?gameName=" + gameName,
        success: function(data) {
            window.location = "index.html";
        },
        error: function(error) {
        }
    });
}

function displayBackToMenuButton() {
    var backToMenuButton = "<br><br><button class=button wood id=back-to-menu-button onClick=goBackToMenu()>Back To Menu</button>";
    $("#back-to-menu-button-div").append(backToMenuButton);
}

function goBackToMenu() {
    var gameName = $("#game-name").text();
    ajaxAfterGameEnded(gameName);
}

function disableGameScreenElements(boardSize) {
    disableBoardElements(boardSize);
    disableButton("quit-button");
}

function disableBoardElements(boardSize) {
    disableClickableCells(boardSize);
    diasbleDiceElements();
}

function diasbleDiceElements() {
    disableButton("roll-dice-button");
    $("#dice-result").css("color", "gray");

}

function disableClickableCells(boardSize) {
    for (var i = 0; i < boardSize - 1; i++) {
        var cellNum = i + 1;
        $("#" + cellNum).removeAttr("class");
        $("#" + cellNum).removeAttr("onclick");
        $("#" + cellNum).unbind('mouseenter mouseleave');
        setCellOriginalColor(cellNum);
    }
}

function setCellOriginalColor(cellNum) {
    if (isEven(cellNum)) {
        $("#" + cellNum).css("background-color", "#f9e2a3");
    } else {
        $("#" + cellNum).css("background-color", "snow");
    }
}

function setClickableCells(inWhichCellsCurrPlayerHasTokens, diceResult) {
    var boardSize = inWhichCellsCurrPlayerHasTokens.length;
    var cellNum;
    var cellsWithTokens = [];
    var gameName = $("#game-name").text();
    for (var i = 0; i < boardSize - 1; i++) {
        if (inWhichCellsCurrPlayerHasTokens[i] === true) {
            var cellNum = i + 1;
            cellsWithTokens.push(cellNum);
            $("#" + cellNum).attr("class", "clickme");
            $("#" + cellNum).attr("onclick", "playHumanTurn(" + cellNum + ")");
            $("#" + cellNum).css("background-color", "#fbd6d6");
            $("#" + cellNum).hover(ajaxMarkNextCell(gameName, cellNum, diceResult, cellsWithTokens));
        }
    }
}

function ajaxMarkNextCell(gameName, hoveredCellNum, diceResult, cellsWithTokens) {
    $.ajax({
        data: "gameName=" + gameName + "&hoveredCellNum=" + hoveredCellNum + "&diceResult=" + diceResult,
        url: "nextCell",
        dataType: 'json',
        success: function(cellsData) {
            $("#" + hoveredCellNum).mouseenter(function() {
                $("#" + cellsData.nextCellNumber).css("background-color", "#bef6df");
            });

            $("#" + hoveredCellNum).mouseleave(function() {
                setCellColorOnMouseLeave(cellsData, hoveredCellNum, cellsWithTokens);
            });
        },
        error: function(error) {
        }
    });
}

function setCellColorOnMouseLeave(cellsData, hoveredCellNum, cellsWithTokens) {
    if (nextCellAlsoHasTokens(cellsData.nextCellNumber, cellsWithTokens)) {
        $("#" + cellsData.nextCellNumber).css("background-color", "#fbd6d6");
    } else {
        if (isEven(hoveredCellNum)) {
            $("#" + cellsData.nextCellNumber).css("background-color", "#f9e2a3");
        } else {
            $("#" + cellsData.nextCellNumber).css("background-color", "snow");
        }
        if (isEven(cellsData.nextCellNumber)) {
            $("#" + cellsData.nextCellNumber).css("background-color", "#f9e2a3");
        } else {
            $("#" + cellsData.nextCellNumber).css("background-color", "snow");
        }
    }
}

function nextCellAlsoHasTokens(nextCellNumber, cellsWithTokens) {
    return ($.inArray(nextCellNumber, cellsWithTokens) !== -1);
}

function updateScreen(data) {
    updatePlayersNamesLabels(data.quittedPlayersNumbers);
    updateCurrPlayerLabel(data.currPlayerName);
    updateSnlLabel(data);
    updateBoard(data);
}

function updateCurrPlayerLabel(currPlayerName) {
    $("#curr-player-label").text("");
    $("#curr-player-label").text("Current Player: " + currPlayerName);
}

function updateSnlLabel(data) {
    if (data.currMoveInfo.usedSnake === true) {
        $("#left-snl-label").text("SNAKE :(");
        $("#right-snl-label").text("SNAKE :(");
    } else if (data.currMoveInfo.usedLadder === true) {
        $("#left-snl-label").text("Ladder!!!");
        $("#right-snl-label").text("Ladder!!!");
    } else {
        $("#left-snl-label").text("");
        $("#right-snl-label").text("");
    }
}

function updatePlayersNamesLabels(quittedPlayersNumbers) {
    for (var i = 0; i < quittedPlayersNumbers.length; i++) {
        $("#player" + quittedPlayersNumbers[i] + "-label").remove();
    }
}

function updateBoard(data) {
    updateLastMove(data);
    updateTurnDescriptionLabel(data);
    hideTokensOfQuittedPlayers(data.quittedPlayersNumbers);
}

function updateTurnDescriptionLabel(data) {
    $("#turn-description").html(data.currMoveInfo.playerMoveStr);
}

function updateLastMove(data) {
    if (data.currMoveInfo.isInstanceInitialized === true) {
        updatePreviousCell(data);
        updateNextCell(data);
    }
}

function updatePreviousCell(data) {
    var playerNumber = data.currMoveInfo.playerNumber;
    var previousCell = data.currMoveInfo.previousCell;
    var tokenImgInPreviousCell = document.getElementById('div' + previousCell +
            '-tokens-images').getElementsByTagName('img')[playerNumber - 1];
    var numOfTokensInPreviousCell = document.getElementById('div' + previousCell +
            '-tokens-images').getElementsByTagName('p')[playerNumber - 1];
    if (data.currMoveInfo.tokensLeftInPreviousCell === 0) {
        tokenImgInPreviousCell.style.visibility = "hidden";
        numOfTokensInPreviousCell.style.visibility = "hidden";
    } else {
        numOfTokensInPreviousCell.innerHTML = data.currMoveInfo.tokensLeftInPreviousCell;
    }
}

function updateNextCell(data) {
    var playerNumber = data.currMoveInfo.playerNumber;
    var nextCell = data.currMoveInfo.nextCell;
    var tokenImgInNextCell = document.getElementById('div' + nextCell +
            '-tokens-images').getElementsByTagName('img')[playerNumber - 1];
    var numOfTokensInNextCell = document.getElementById('div' + nextCell +
            '-tokens-images').getElementsByTagName('p')[playerNumber - 1];
    tokenImgInNextCell.style.visibility = "visible";
    numOfTokensInNextCell.style.visibility = "visible";
    numOfTokensInNextCell.innerHTML = data.currMoveInfo.tokensInNextCell;
}


function hideTokensOfQuittedPlayers(quittedPlayersNumbers) {
    var quittedPlayerTokens;
    var quittedPlayerTokensParagraphs;
    for (var i = 0; i < quittedPlayersNumbers.length; i++) {
        quittedPlayerTokens = document.getElementsByClassName("player" + quittedPlayersNumbers[i] + "-token");
        quittedPlayerTokensParagraphs = document.getElementsByClassName("p" + quittedPlayersNumbers[i]);
        for (var j = 0; j < quittedPlayerTokens.length; j++) {
            quittedPlayerTokens[j].style.visibility = "hidden";
            quittedPlayerTokensParagraphs[j].style.visibility = "hidden";
        }
    }
}

function onClickQuitGame() {
    var gameName = $("#game-name").text();
    ajaxQuitGame(gameName);
}

function ajaxQuitGame(gameName) {
    $.ajax({
        url: "playerQuits?gameName=" + gameName,
        success: function(isNextPlayerComputer) {
            if (isNextPlayerComputer === "true") {
                ajaxPlayComputerTurn(gameName);
            }
            window.location = "index.html";
        },
        error: function(error) {
        }
    });
}


function drawBoard(initialBoardStatus, boardDimension, snakes, ladders) {
    var index = 0, indexHelper;
    var cellNumber;
    var evenRowNumberBool;
    var evenBoardDimensionBool;
    var htmlBoardStr = "<table id=board-table border=\"1\" >";
    for (var i = boardDimension; i > 0; i--) {
        htmlBoardStr += "<tr height=\"70\">";
        indexHelper = 1;
        for (var j = boardDimension; j > 0; j--) {
            evenRowNumberBool = isEven(boardDimension - i);
            evenBoardDimensionBool = isEven(boardDimension);
            index = calcIndexBoardDimension(boardDimension, i, j, evenRowNumberBool, evenBoardDimensionBool, indexHelper);
            cellNumber = index + 1;
            htmlBoardStr += "<td style=background-color:";
            if (isEven(cellNumber)) {
                htmlBoardStr += "#f9e2a3 ";
            } else {
                htmlBoardStr += "snow ";
            }

            htmlBoardStr += "id=" + cellNumber + "\><div class=snl-img id=snl" + cellNumber +
                    "-img></div><div id=div" + cellNumber + "-tokens-images><img src=images/player1token.png class=player1-token><p class=p1>" +
                    initialBoardStatus[index][0] + "</p><img src=images/player2token.png class=player2-token><p class=p2>" +
                    initialBoardStatus[index][1] + "</p><img src=images/player3token.png class=player3-token><p class=p3>" +
                    initialBoardStatus[index][2] + "</p><img src=images/player4token.png class=player4-token><p class=p4>" +
                    initialBoardStatus[index][3] + "</p></div>" + cellNumber + "</td>";
            indexHelper++;
        }
        htmlBoardStr += "</tr>";
    }
    htmlBoardStr += "</table>";
    document.getElementById("board").innerHTML = htmlBoardStr;
    changeTokensSize(boardDimension);
    drawSnakesAndLadders(snakes, ladders);
}

function changeTokensSize(boardDimension) {
    var LARGEST_BOARD_DIMENSION = 8;
    if (boardDimension === LARGEST_BOARD_DIMENSION) {
        updateTokensImagesSize();
    }
}

function isEven(number) {
    return (number % 2 === 0);
}

function updateTokensImagesSize() {
    updateTokenImgAndParagraph('p1', 'player1-token');
    updateTokenImgAndParagraph('p2', 'player2-token');
    updateTokenImgAndParagraph('p3', 'player3-token');
    updateTokenImgAndParagraph('p4', 'player4-token');
}

function updateTokenImgAndParagraph(paragraphClassName, tokenImgClassName) {
    var playerTokensImages = document.getElementsByClassName(tokenImgClassName);
    for (var i = 0; i < playerTokensImages.length; i++) {
        updateTokensImagesSizes(i, tokenImgClassName);
        updateNumOfTokensParagraph(i, paragraphClassName);
    }
}

function updateTokensImagesSizes(i, tokenImgClassName) {
    document.getElementsByClassName(tokenImgClassName)[i].style.width = '15px';
    document.getElementsByClassName(tokenImgClassName)[i].style.height = '22px';
}

function updateNumOfTokensParagraph(i, paragraphClassName) {
    document.getElementsByClassName(paragraphClassName)[i].style.marginTop = "-21px";
    switch (paragraphClassName) {
        case "p1":
            document.getElementsByClassName(paragraphClassName)[i].style.marginLeft = "6px";
            break;
        case "p2":
            document.getElementsByClassName(paragraphClassName)[i].style.marginLeft = "22px";
            break
        case "p3":
            document.getElementsByClassName(paragraphClassName)[i].style.marginLeft = "38px";
            break;
        case "p4":
            document.getElementsByClassName(paragraphClassName)[i].style.marginLeft = "54px";
            break;
        default:
            break;
    }
}

function drawSnakesAndLadders(snakes, ladders) {

    for (var i = 0; i < snakes.length; i++) {
        addSnakeOrLadder(snakes[i], "snake");
        addSnakeOrLadder(ladders[i], "ladder");
    }
}

function addSnakeOrLadder(bridge, bridgeType) {
    var cell1, cell2;
    if (bridgeType === "snake") {
        cell1 = document.getElementById(bridge.headLocation);
        cell2 = document.getElementById(bridge.tailLocation);
    } else if (bridgeType === "ladder") {
        cell1 = document.getElementById(bridge.topLocation);
        cell2 = document.getElementById(bridge.bottomLocation);
    }

    connect(cell1, cell2, bridgeType);
}

function getOffset(element) {
    var x = $(element).offset().left;
    var y = $(element).offset().top;
    var width = $(element).width();
    var height = $(element).height();
    return {top: y, left: x, width: width, height: height};
}

function connect(div1, div2, bridgeType) {
    var off1 = getOffset(div1);
    var off2 = getOffset(div2);
    var x1 = off1.left + (off1.width / 2);
    var y1 = off1.top + (off1.height / 2);
    var x2 = off2.left + (off2.width / 2);
    var y2 = off2.top + (off2.height / 2);
    var length = calcDistanceBetweenTwoCells(x1, y1, x2, y2);
    var cx = ((x1 + x2) / 2);
    var cy = ((y1 + y2) / 2) - (length / 2);
    var angle = getAngleOfLineBetweenTwoPoints(x1, y1, x2, y2);
    var imgWidth = setImageWidth(bridgeType);
    var htmlLine = "<img class=snl src=images/" + bridgeType + ".png style='height:" + length + "px; line-height:1px; position:absolute; left:" + cx + "px; top:" + cy + "px; width:" + imgWidth + "px; -moz-transform:rotate(" + angle + "deg); -webkit-transform:rotate(" + angle + "deg); -o-transform:rotate(" + angle + "deg); -ms-transform:rotate(" + angle + "deg); transform:rotate(" + angle + "deg);' />";
    document.body.innerHTML += htmlLine;
}

function setImageWidth(bridgeType) {
    var imgWidth;
    if (bridgeType === "snake") {
        imgWidth = 25;
    } else if (bridgeType === "ladder") {
        imgWidth = 33;
    }

    return imgWidth;
}

function calcDistanceBetweenTwoCells(x1, y1, x2, y2) {
    var CALC_POWER = 2;
    return Math.sqrt(Math.pow(x1 - x2, CALC_POWER)
            + Math.pow(y1 - y2, CALC_POWER));
}

function getAngleOfLineBetweenTwoPoints(x1, y1, x2, y2) {
    var HALF_CIRCLE = 180;
    var RIGHT_ANGLE = 90;
    var angle;
    angle = Math.atan2((y1 - y2), (x1 - x2)) * (HALF_CIRCLE / Math.PI) + RIGHT_ANGLE;
    return angle;
}

function setVisibleTokens(boardSize, initialBoardStatus, numOfPlayers) {
    var img;
    var paragraph;
    for (var cellIndex = 0; cellIndex < boardSize; cellIndex++) {
        var cellNumber = cellIndex + 1;
        for (var playerIndex = 0; playerIndex < numOfPlayers; playerIndex++) {
            img = document.getElementById('div' + cellNumber + '-tokens-images').getElementsByTagName('img')[playerIndex];
            paragraph = document.getElementById('div' + cellNumber + '-tokens-images').getElementsByTagName('p')[playerIndex];
            if (initialBoardStatus[cellIndex][playerIndex] > 0) {
                img.style.visibility = 'visible';
                paragraph.style.visibility = 'visible';
            } else {
                img.style.visibility = 'hidden';
                paragraph.style.visibility = 'hidden';
            }
        }
    }
}

function calcIndexBoardDimension(boardDimension, row, col, evenRowNumber, evenBoardDimension, indexHelper) {
    var index;
    if (evenBoardDimension) {
        if (!evenRowNumber) {
            index = boardDimension * row - col;
        } else {
            index = boardDimension * row - indexHelper;
        }
    } else {
        if (evenRowNumber) {
            index = boardDimension * row - col;
        } else {
            index = boardDimension * row - indexHelper;
        }
    }
    return index;
}

function onRollDiceClick() {
    var gameName = $("#game-name").text();
    disableButton("roll-dice-button");
    eraseLabelsText();
    ajaxGetDiceResult(gameName);
}

function ajaxGetDiceResult(gameName) {
    jQuery.ajax({
        url: "rollDice?gameName=" + gameName,
        dataType: 'json',
        timeout: TIMEOUT_RATE,
        success: function(diceData) {
            playGameSounds("dice_sound.mp3");
            rollDiceAndSetClickableCells(showRandomNumbers, DELAY_BETWEEN_DICE_RESULTS,
                    NUM_OF_RANDOM_DICE_RESULTS, diceData);
        },
        error: function(error) {

        }
    });
}

function eraseLabelsText() {
    $("#left-snl-label").text("");
    $("#right-snl-label").text("");
    $("#turn-description").html("");
}

function showRandomNumbers() {
    var randomNumber = Math.floor(Math.random() * (6) + 1);
    $("#dice-result").text(randomNumber);
}

function rollDiceAndSetClickableCells(callback, delay, repetitions, diceData) {
    var x = 0;
    var intervalID = setInterval(function() {
        var diceResult = diceData.diceResult;
        callback();
        if (++x === repetitions) {
            clearInterval(intervalID);
            $("#dice-result").text(diceResult);
            setClickableCells(diceData.inWhichCellsCurrPlayerHasTokens, diceResult);
            $("#dice-result-input").val(diceResult);
        }
    }, delay);
}

function playHumanTurn(chosenCellNumber) {
    disableClickableCells(boardSizeGlobal);
    document.getElementById('cell-number').value = chosenCellNumber;
    ajaxPlayHumanTurn();
}

function ajaxPlayHumanTurn() {
    jQuery.ajax({
        data: $("#chosen-cell-form").serialize(),
        url: "playHumanTurn",
        timeout: TIMEOUT_RATE,
        error: function() {
            console.error("failed to submit");
        },
        success: function(isNextPlayerComputer) {
            triggerAjaxRefreshScreen();
            if (isNextPlayerComputer === "true") {
                var gameName = $("#game-name").text();
                ajaxPlayComputerTurn(gameName);
            }
        }
    });
}

function ajaxPlayComputerTurn(gameName) {
    jQuery.ajax({
        data: $("#chosen-cell-form").serialize(),
        url: "playComputerTurn?gameName=" + gameName,
        error: function() {
            console.error("failed to submit");
        },
        success: function(isNextPlayerComputer) {
            if (isNextPlayerComputer === "true") {
                var gameName = $("#game-name").text();
                ajaxPlayComputerTurn(gameName);
            }
        }
    });
}


