package com.shao.beautiful.tools;

import com.shao.beautiful.gameObj.FaceData;
import java.util.HashMap;

public class FaceDataManager {

    private static HashMap<Integer,FaceData> BLOCKONLY=new HashMap<>();
    public static FaceData get(int id) {
        if (BLOCKONLY.containsKey(id)) {
            return BLOCKONLY.get((id));
        } else {
            FaceData texx=new FaceData(id);

            BLOCKONLY.put(id, texx);

            return texx;
        }
    }

}
