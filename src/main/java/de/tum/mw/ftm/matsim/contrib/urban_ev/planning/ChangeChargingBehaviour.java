package de.tum.mw.ftm.matsim.contrib.urban_ev.planning;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.replanning.PlanStrategyImpl;
import org.matsim.core.replanning.selectors.ExpBetaPlanSelector;

import javax.inject.Inject;
import javax.inject.Provider;

public class ChangeChargingBehaviour implements Provider<PlanStrategy> {

    private EventsManager eventsManager;
    private Scenario scenario;

    @Inject
    public ChangeChargingBehaviour(EventsManager eventsManager, Scenario scenario) {
        this.eventsManager = eventsManager;
        this.scenario = scenario;
    }

    @Override
    public PlanStrategy get() {
        double logitScaleFactor = 1.0;
        PlanStrategyImpl.Builder builder = new PlanStrategyImpl.Builder(new ExpBetaPlanSelector<>(logitScaleFactor));
        ChangeChargingBehaviourModule changeChargingBehaviourModule = new ChangeChargingBehaviourModule(scenario);
        builder.addStrategyModule(changeChargingBehaviourModule);
        eventsManager.addHandler(changeChargingBehaviourModule);
        return builder.build();
    }

}
