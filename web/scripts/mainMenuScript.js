/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function playOrPauseMusic() {
    var myAudio = document.getElementById("game-audio");
    var audioImage = document.getElementById("audio-image");

    if (myAudio.paused) {
        myAudio.play();
        audioImage.src = "./images/audio_image.png";

    } else {
        myAudio.pause();
        audioImage.src = "./images/no_audio_image.png";    
    }
}
