/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.landleaf.normal.plc;

/**
*
* @author Dave Nardella
*/

public class IntByRef {
   
    public IntByRef(int Val)
    {
        this.Value=Val;
    }
    public IntByRef()
    {
        this.Value=0;
    }
    public int Value;
}
