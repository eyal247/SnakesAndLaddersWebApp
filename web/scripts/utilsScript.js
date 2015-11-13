/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function disableButton(buttonIdStr) {
    document.getElementById(buttonIdStr).disabled = true;
    setOpacity("#" + buttonIdStr, 0.5);
}

function activateButton(buttonIdStr) {
    document.getElementById(buttonIdStr).disabled = false;
    setOpacity("#" + buttonIdStr, 1.0);
}

function setOpacity(idStr, opacityValue) {
    $(idStr).css({opacity: opacityValue});
}
