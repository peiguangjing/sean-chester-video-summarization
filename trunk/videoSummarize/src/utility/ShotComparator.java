package utility;

import java.util.Comparator;

import shot.Shot;

public class ShotComparator implements Comparator<Shot>{
	@Override
    public int compare(Shot lhs, Shot rhs)
    {
        if (lhs.GetShotImportance() > rhs.GetShotImportance())
        {
            return -1;
        }
        else if (lhs.GetShotImportance() < rhs.GetShotImportance())
        {
            return 1;
        }
        return 0;
    }
}
