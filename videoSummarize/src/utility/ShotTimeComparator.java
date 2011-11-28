package utility;

import java.util.Comparator;

import shot.Shot;

public class ShotTimeComparator implements Comparator<Shot>{
	@Override
    public int compare(Shot lhs, Shot rhs)
    {
        if (lhs.StartFrame() < rhs.StartFrame())
        {
            return -1;
        }
        else if (lhs.StartFrame() > rhs.StartFrame())
        {
            return 1;
        }
        return 0;
    }
}
