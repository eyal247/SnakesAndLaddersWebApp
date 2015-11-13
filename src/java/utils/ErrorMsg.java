/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import static utils.UtilsConstants.VALID_INPUT;

/**
 *
 * @author EyalEngel
 */
public class ErrorMsg {
    private final String errorMsg;

    public ErrorMsg(String errorMsg) {
        if (errorMsg == null) {
            this.errorMsg = VALID_INPUT;
        } else {
            this.errorMsg = errorMsg;
        }
    }
}
