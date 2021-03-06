package ai.labs.behavior.impl.extensions;

import ai.labs.behavior.impl.BehaviorRule;
import ai.labs.memory.IConversationMemory;
import ai.labs.memory.IData;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author ginccc
 */
@NoArgsConstructor
public class Occurrence implements IBehaviorExtension {
    public static final String ID = "occurrence";
    private static final String BEHAVIOR_RULES_SUCCESS = "behavior_rules:success";

    private final String behaviorRuleNameQualifier = "behaviorRuleName";
    private String behaviorRuleName;

    private final String maxOccurrenceQualifier = "maxOccurrence";
    @Setter
    private int maxOccurrence = 1;

    private ExecutionState state = ExecutionState.NOT_EXECUTED;


    private int countOccurrences(List<List<String>> allBehaviorRulesHistorical) {
        int occurrences = 0;
        for (List<String> history : allBehaviorRulesHistorical) {
            for (String behaviorRuleName : history) {
                if (this.behaviorRuleName.equals(behaviorRuleName)) {
                    occurrences++;
                }
            }
        }

        return occurrences;
    }

    private List<List<String>> getAllBehaviorRules(List<List<IData<List<String>>>> allData) {
        List<List<String>> allBehaviorRules = new LinkedList<>();
        for (List<IData<List<String>>> dataList : allData) {
            for (IData<List<String>> data : dataList) {
                allBehaviorRules.add(data.getResult());
            }
        }

        return allBehaviorRules;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Map<String, String> getValues() {
        HashMap<String, String> result = new HashMap<>();
        result.put(maxOccurrenceQualifier, String.valueOf(maxOccurrence));
        result.put(behaviorRuleNameQualifier, behaviorRuleName);
        return result;
    }

    @Override
    public void setValues(Map<String, String> values) {
        if (values != null && !values.isEmpty()) {
            if (values.containsKey(behaviorRuleNameQualifier)) {
                behaviorRuleName = values.get(behaviorRuleNameQualifier);
            }

            if (values.containsKey(maxOccurrenceQualifier)) {
                String occurrenceValue = values.get(maxOccurrenceQualifier);
                if ("ever".equals(occurrenceValue)) {
                    setMaxOccurrence(-1);
                } else {
                    setMaxOccurrence(Integer.parseInt(occurrenceValue));
                }
            }
        }
    }

    @Override
    public ExecutionState execute(IConversationMemory memory, List<BehaviorRule> trace) {
        boolean success;
        List<List<IData<List<String>>>> allData = memory.getAllSteps().getAllData(BEHAVIOR_RULES_SUCCESS);
        if (allData != null) {
            int actualOccurrences = countOccurrences(getAllBehaviorRules(allData));
            if (maxOccurrence == -1) {
                //it did occurred at least once
                success = actualOccurrences > 0;
            } else {
                success = maxOccurrence >= actualOccurrences;
            }
        } else {
            success = false;
        }

        if (success) {
            state = ExecutionState.SUCCESS;
        } else {
            state = ExecutionState.FAIL;
        }

        return state;
    }

    @Override
    public ExecutionState getExecutionState() {
        return state;
    }

    @Override
    public IBehaviorExtension clone() throws CloneNotSupportedException {
        IBehaviorExtension occurrence = new Occurrence();
        occurrence.setValues(getValues());

        return occurrence;
    }
}
