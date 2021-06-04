package com.landleaf.normal.plc;


import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.landleaf.normal.plc.PlcUnitType.VB;
import static com.landleaf.normal.plc.PlcUnitType.VD;
import static com.landleaf.normal.plc.PlcUnitType.VW;


@StringDef({VD, VW, VB})
@Retention(RetentionPolicy.SOURCE)
public @interface UnitType {
}