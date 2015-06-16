/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.scheduledactions

import rx.functions.Action1
/**
 *
 * @author sthadeshwar
 */
class ActionInstanceSpec extends ModelSpec {

    static class TestAction extends ActionSupport {
        void execute(Context context, Execution execution) throws Exception {
        }
    }

    static class TestAction1 implements Action1<ActionInstance> {
        @Override
        public void call(ActionInstance actionInstance) {
        }
    }

    void 'serialize and deserialize for ActionInstance should not throw any exceptions1'() {
        setup:
        String id = UUID.randomUUID().toString()
        String name = 'CanaryAnalysisAction'
        String cron = '0 0/10 * * * ? *'
        ActionInstance actionInstance = ActionInstance.newActionInstance()
            .withName(name)
            .withGroup(id)
            .withAction(TestAction.class)
            .withParameters([canaryId: id])
            .withTrigger(new CronTrigger(cron))
            .withOwners('sthadeshwar@netflix.com')
            .withWatchers('sthadeshwar@netflix.com')
            .withExecutionTimeoutInSeconds(20)
            .build()
        actionInstance.id = id
        actionInstance.context = ActionInstance.createContext(actionInstance)
        com.netflix.fenzo.triggers.Trigger fenzoTrigger = actionInstance.getTrigger().createFenzoTrigger(actionInstance.context, TestAction1.class)
        actionInstance.setFenzoTrigger(fenzoTrigger)

        when:
        byte[] bytes = objectMapper.writeValueAsBytes(actionInstance)

        then:
        noExceptionThrown()
        bytes != null

        when:
        ActionInstance deserializedActionInstance = objectMapper.readValue(bytes, ActionInstance.class)

        then:
        noExceptionThrown()
        deserializedActionInstance != null
        deserializedActionInstance.name == actionInstance.name
        deserializedActionInstance.group == actionInstance.group
        deserializedActionInstance.action == actionInstance.action
        deserializedActionInstance.trigger != null
        deserializedActionInstance.trigger instanceof CronTrigger
        ((CronTrigger) deserializedActionInstance.trigger).cronExpression == cron
        deserializedActionInstance.fenzoTrigger != null
        deserializedActionInstance.fenzoTrigger instanceof com.netflix.fenzo.triggers.CronTrigger
    }

    void 'serialize and deserialize for ActionInstance should not throw any exceptions'() {
        setup:
        String id = UUID.randomUUID().toString()
        String name = 'CanaryAnalysisAction'
        String cron = '0 0/10 * * * ? *'
        ActionInstance actionInstance = ActionInstance.newActionInstance()
            .withName(name)
            .withGroup(id)
            .withAction(TestAction.class)
            .withParameters([canaryId: id])
            .withTrigger(new CronTrigger(cron))
            .withOwners('sthadeshwar@netflix.com')
            .withWatchers('sthadeshwar@netflix.com')
            .withExecutionTimeoutInSeconds(20)
            .build()
        actionInstance.id = id
        actionInstance.context = ActionInstance.createContext(actionInstance)
        com.netflix.fenzo.triggers.Trigger fenzoTrigger = actionInstance.getTrigger().createFenzoTrigger(actionInstance.context, TestAction1.class)
        actionInstance.setFenzoTrigger(fenzoTrigger)

        when:
        String bytes = objectMapper.writeValueAsString(actionInstance)

        then:
        noExceptionThrown()
        bytes != null

        when:
        ActionInstance deserializedActionInstance = objectMapper.readValue(bytes, ActionInstance.class)

        then:
        noExceptionThrown()
        deserializedActionInstance != null
        deserializedActionInstance.name == actionInstance.name
        deserializedActionInstance.group == actionInstance.group
        deserializedActionInstance.action == actionInstance.action
        deserializedActionInstance.trigger != null
        deserializedActionInstance.trigger instanceof CronTrigger
        ((CronTrigger) deserializedActionInstance.trigger).cronExpression == cron
        deserializedActionInstance.fenzoTrigger != null
        deserializedActionInstance.fenzoTrigger instanceof com.netflix.fenzo.triggers.CronTrigger
    }
}