package Utilities;
import com.slayed.life.engage.R;

import java.util.ArrayList;
import java.util.List;

import Base.BaseEngage;
import static com.slayed.life.engage.Level.*;

public class Utilities {

    public static String getUserLevelName() {
        switch (BaseEngage.user.level) {
            case Bronze:
            default:
                return "Bronze";
            case Silver:
                return "Silver";
            case Gold:
                return "Gold";
            case Diamond:
                return "Diamond";
            case Platinum:
                return "Platinum";
            case Rhodium:
                return "Rhodium";
        }
    }

    public static String getNextLevelName() {
        switch (BaseEngage.user.level) {
            case Bronze:
            default:
                return "Silver";
            case Silver:
                return "Gold";
            case Gold:
                return "Diamond";
            case Diamond:
                return "Platinum";
            case Platinum:
                return "Rhodium";
            case Rhodium:
                return "Engaged!";
        }
    }

    public static int getMaxLevel() {
        switch (BaseEngage.user.level) {
            case Bronze:
            default:
                return 100000;
            case Silver:
                return 500000;
            case Gold:
                return 1000000;
            case Diamond:
                return 10000000;
            case Platinum:
                return 100000000;
            case Rhodium:
                return 500000000;
        }
    }
}
