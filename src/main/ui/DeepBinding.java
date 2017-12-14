package main.ui;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

import static javafx.beans.binding.Bindings.greaterThanOrEqual;
import static main.ui.Controller.onUpdate;

/**
 * A DeepBinding represents a binding to an ObservableValue nested within another ObservableValue. The DeepBinding
 * updates whenever either ObservableValue updates.
 *
 *     Foo foo = new Foo();                                 // foo has property 'bar', bar has property 'another'
 *     Binding<Boolean> binding = new DeepBinding<>(
 *             foo.barProperty(),                           // the root value
 *             bar -> bar.anotherProperty(),                // the nested value
 *             anotherProperty -> anotherProperty.isNull()  // the actual binding on the nested value
 *     );
 *
 * Using method references, this can be condensed to:
 *
 *     Foo foo = new Foo();
 *     Binding<Boolean> binding = new DeepBinding<>(foo.barProperty(), Bar::anotherProperty, ObjectExpression::isNull);
 *
 * These examples are roughly analogous to:
 *
 *     Foo foo = new Foo();
 *     Binding<Boolean> = Bindings.createBooleanBinding(
 *             () -> foo.getBar().anotherProperty().isNull(),
 *             foo.barProperty(), foo.getBar().anotherProperty()
 *     );
 *
 * Except that the binding will always reflect the current value of foo.getBar().getAnother(), even when foo.bar or
 * foo.bar.another are updated.
 *
 * @author Anthony
 * @since 12/13/2017
 */
public class DeepBinding<T,U,R> extends ObjectBinding<R> {

    private ObservableValue<T> first;
    private ObservableValue<U> second;
    private ObservableValue<R> third;
    private Function<T, ObservableValue<U>> func1;
    private Function<ObservableValue<U>,ObservableValue<R>> func2;

    /**
     * Construct a new binding to func2(func1(root)).
     * @param root the root value
     * @param func1 a function that accepts the root value and returns the nested value
     * @param func2 a function that accepts the nested value and returns the desired output
     */
    public DeepBinding(ObservableValue<T> root, Function<T, ObservableValue<U>> func1,
                       Function<ObservableValue<U>, ObservableValue<R>> func2) {
        this.func1 = func1;
        this.func2 = func2;
        setFirst(root);
    }

    private void setFirst(ObservableValue<T> first) {
        this.first = first;
        onUpdate(first, x -> {setSecond(); invalidate();});
        setSecond();
    }

    private void setSecond() {
        second = func1.apply(first.getValue());
        onUpdate(second, x -> {setThird(); invalidate();});
        setThird();
    }

    private void setThird() {
        third = func2.apply(second);
        onUpdate(third, x -> invalidate());
    }

    @Override
    protected R computeValue() {
        return third.getValue();
    }

    public static void main(String[] args) {
        NewLocation loc = new NewLocation(20);
        DeepBinding<NewAccount, Number, Boolean> binding = new DeepBinding<>(
                loc.wageSourceAccountProperty(),
                NewAccount::balanceProperty,
                bal -> greaterThanOrEqual((DoubleProperty) bal, loc.totalWagesProperty())
        );
        System.out.println("Balance >= Wages: " + binding.get());
        System.out.println("Wages: " + 200);
        loc.totalWages.set(200);
        System.out.println("Balance >= Wages: " + binding.get());
        loc.wageSourceAccount.set(new NewAccount(500));
        System.out.println("Balance >= Wages: " + binding.get());
        System.out.println("Balance: " + 0);
        loc.wageSourceAccount.get().balance.set(0);
        System.out.println("Balance >= Wages: " + binding.get());
    }

    private static class NewAccount {
        private DoubleProperty balance;

        public NewAccount(double value) {
            balance = new SimpleDoubleProperty(value);
        }

        public DoubleProperty balanceProperty() {
            return balance;
        }
    }

    private static class NewLocation {
        private DoubleProperty totalWages;
        private ObjectProperty<NewAccount> wageSourceAccount;

        public NewLocation(double wages) {
            totalWages = new SimpleDoubleProperty(wages);
            wageSourceAccount = new SimpleObjectProperty<>(new NewAccount(100));
        }

        public DoubleProperty totalWagesProperty() {
            return totalWages;
        }

        public ObjectProperty<NewAccount> wageSourceAccountProperty() {
            return wageSourceAccount;
        }
    }

}
