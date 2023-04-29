package org.starloco.locos.quest;

import java.util.HashSet;
import java.util.Set;

public class QuestProgress {
    public final int entityId;
    public final int questId;
    private boolean finished;
    private int currentStep;
    private final Set<Integer> completedObjectives;

    public QuestProgress(int entityId, int questId, int sId) {
        this.entityId = entityId;
        this.questId = questId;
        this.currentStep = sId;
        this.completedObjectives = new HashSet<>();
    }

    public QuestProgress(int playerId, int questId, int sId, Set<Integer> completedObjectives, boolean finished) {
        this.entityId = playerId;
        this.questId = questId;
        this.currentStep = sId;
        this.completedObjectives = completedObjectives;
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int step) {
        this.completedObjectives.clear();
        this.currentStep = step;
    }

    public boolean hasCompletedObjective(int objectiveId) {
        return completedObjectives.contains(objectiveId);
    }

    public void completeObjective(int objectiveId) {
        completedObjectives.add(objectiveId);
    }

    public Set<Integer> getCompletedObjectives() {
        return this.completedObjectives;
    }

    public void markFinished() {
        this.currentStep = 0;
        this.finished = true;
    }

    public int getQuestId() {
        return this.questId;
    }
}
