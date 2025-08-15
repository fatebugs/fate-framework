package com.fatebug.base.core.valid.order;

import com.fatebug.base.core.valid.groups.SearchGroups;
import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence(
        {
                Default.class,
                SearchGroups.GroupA.class,
                SearchGroups.GroupB.class,
                SearchGroups.GroupC.class,
                SearchGroups.GroupD.class,
                SearchGroups.GroupE.class,
                SearchGroups.GroupF.class,
                SearchGroups.GroupG.class,
                SearchGroups.GroupH.class,
                SearchGroups.GroupI.class,
                SearchGroups.GroupJ.class,
                SearchGroups.GroupK.class,
                SearchGroups.GroupL.class,
                SearchGroups.GroupM.class,
                SearchGroups.GroupN.class,
                SearchGroups.GroupO.class,
                SearchGroups.GroupP.class,
                SearchGroups.GroupQ.class,
                SearchGroups.GroupR.class,
                SearchGroups.GroupS.class,
                SearchGroups.GroupT.class,
                SearchGroups.GroupU.class,
                SearchGroups.GroupV.class,
                SearchGroups.GroupW.class,
                SearchGroups.GroupX.class,
                SearchGroups.GroupY.class,
                SearchGroups.GroupZ.class
        })
public interface SearchOrder {}