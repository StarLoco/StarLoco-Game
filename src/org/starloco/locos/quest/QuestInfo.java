package org.starloco.locos.quest;

import java.util.Collections;
import java.util.List;

public class QuestInfo {
    public final List<Integer> objectives;
    public final boolean isAccountBound, isRepeatable;
    public final Integer previous;
    public final Integer next;
    public final Integer question;

    public QuestInfo(List<Integer> objectives, Integer previous, Integer next, Integer question, boolean isAccountBound, boolean isRepeatable) {
        this.objectives = Collections.unmodifiableList(objectives);
        this.previous = previous;
        this.next = next;
        this.question = question;
        this.isAccountBound = isAccountBound;
        this.isRepeatable = isRepeatable;
    }
}
