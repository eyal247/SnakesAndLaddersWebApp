/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function setMaxNumOfSnl(boardSize) {
    ajaxNumOfSnl(boardSize);
}

function ajaxNumOfSnl(boardSize) {
    $.ajax({
        url: "numOfSnl?boardSize=" + boardSize,
        dataType: 'json',
        success: function(data) {
            removeHtmlTagsById('#num-of-snl', 'option');
            setNumOfSnlOptions(data.numOfSnl);
        },
        error: function(error) {
        }
    });
}

function setNumOfComputers(numOfPlayersStr) {
    removeHtmlTagsById('#num-of-computers', 'option');
    removeComputersTextBoxes();
    setNumOfComputersOptions(numOfPlayersStr);
}

function removeHtmlTagsById(idStr, tagNameStr) {
    $(idStr)
            .find(tagNameStr)
            .remove()
            .end();
}

function setNumOfComputersOptions(numOfPlayersStr) {
    for (var i = 0; i < parseInt(numOfPlayersStr); i++) {
        $("#num-of-computers").append('<option value=' + i + '>' + i + '</option>');
    }
}

function setNumOfSnlOptions(numOfSnl) {
    for (var i = 1; i <= numOfSnl; i++) {
        $("#num-of-snl").append('<option value=' + i + '>' + i + '</option>');
    }
}

function createComputersTextBoxes(numOfComputers) {
    removeComputersTextBoxes();
    addComputersTextBoxes(numOfComputers);
}

function removeComputersTextBoxes() {
    var computersTextBoxesId = "#computers-text-boxes";

    removeHtmlTagsById(computersTextBoxesId, 'label');
    removeHtmlTagsById(computersTextBoxesId, 'input');
}

function addComputersTextBoxes(numOfComputersStr) {
    for (var i = 0; i < parseInt(numOfComputersStr); i++) {
        var computerNumberStr = i + 1;
        $("#computers-text-boxes").append('<label class=settings-labels>Computer ' + computerNumberStr + ' Name: </label>');
        $("#computers-text-boxes").append('<input class=computers type=text name=computer' + i + '></input>');
    }
}

function onContinueClickedBeforeValidation() {
    jQuery.ajax({
        data: $("#settings-form").serialize(),
        dataType: 'json',
        url: "validateSettings",
        timeout: 2000,
        error: function() {
            console.error("failed to submit");
        },
        success: function(data) {
            if (data.errorMsg === "valid") {
                mySubmit();
            }
            else {
                displayErrorMsg(data.errorMsg);
            }
        }
    });
}

function displayErrorMsg(errorMsg) {
    $("#error-message-container").html("");
    $("#error-message-container").append('<span id=error-message>' + errorMsg + '</span>');
}

function onClickBackToMenu() {
    window.location = "index.html";
}

function mySubmit() {
    $("#settings-form").submit();
}
