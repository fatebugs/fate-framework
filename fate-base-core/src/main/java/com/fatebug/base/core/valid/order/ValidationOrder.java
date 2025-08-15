package com.fatebug.base.core.valid.order;

import com.fatebug.base.core.valid.groups.ValidationGroups;
import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence(
        {
                Default.class,
                ValidationGroups.GroupA.class,
                ValidationGroups.GroupB.class,
                ValidationGroups.GroupC.class,
                ValidationGroups.GroupD.class,
                ValidationGroups.GroupE.class,
                ValidationGroups.GroupF.class,
                ValidationGroups.GroupG.class,
                ValidationGroups.GroupH.class,
                ValidationGroups.GroupI.class,
                ValidationGroups.GroupJ.class,
                ValidationGroups.GroupK.class,
                ValidationGroups.GroupL.class,
                ValidationGroups.GroupM.class,
                ValidationGroups.GroupN.class,
                ValidationGroups.GroupO.class,
                ValidationGroups.GroupP.class,
                ValidationGroups.GroupQ.class,
                ValidationGroups.GroupR.class,
                ValidationGroups.GroupS.class,
                ValidationGroups.GroupT.class,
                ValidationGroups.GroupU.class,
                ValidationGroups.GroupV.class,
                ValidationGroups.GroupW.class,
                ValidationGroups.GroupX.class,
                ValidationGroups.GroupY.class,
                ValidationGroups.GroupZ.class
        })
public interface ValidationOrder {}