package core;

import org.junit.AssumptionViolatedException;
import org.junit.Ignore;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class Junit4Rerunner extends BlockJUnit4ClassRunner {

    public Junit4Rerunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        Description description = this.describeChild(method);
        if (method.getAnnotation(Ignore.class) != null) {
            notifier.fireTestIgnored(description);
        } else {
            runTestWithRetries(methodBlock(method), describeChild(method), notifier);
        }

    }

    private void runTestWithRetries(Statement statement, Description description, RunNotifier runNotifier) {
        EachTestNotifier eachTestNotifier = new EachTestNotifier(runNotifier, description); 
        eachTestNotifier.fireTestStarted(); 
        try { statement.evaluate();
        } catch (AssumptionViolatedException e) { eachTestNotifier.addFailedAssumption(e); } catch (Throwable e) {
            eachTestNotifier.addFailure(e);
        } finally { eachTestNotifier.fireTestFinished();
        } 
    }
}