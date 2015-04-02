# A contracted stack interface #

Let's first define a minimalistic stack interface. A stack should at least have methods to insert a new element on top of it, and remove the topmost element:
```
interface Stack<T> {
  public T pop();
  public void push(T obj);
}
```

If we try to contract this interface, however, we will find no obvious contract to put on it. This is because the interface exposes no query method that lets us examine the state of the object without changing it. In order to establish contracts about the state of a object, we need to be able to query that state.

One simple information we may want to make assertions about is the number of elements currently in a stack. Let's add a size method to our interface:

```
interface Stack<T> {
  public int size();
  public T pop();
  public void push(T obj);
}
```

In Java, `int` is a signed integer type, but it is pretty obvious that a stack can never have a negative number of elements: let's write that as our first contract! Since it is a contract on the state of the object that always holds, it should be a class invariant; class invariants are denoted by the `com.google.java.contract.Invariant` annotation.

```
import com.google.java.contract.Invariant;

@Invariant("size() >= 0")
interface Stack<T> {
  public int size();
  public T pop();
  public void push(T obj);
}
```

N.B.: Notice the quotes around the invariant expression. This is a syntactic artefact due to the way annotations are handled in Java. Annotation values need to be constants and can only be manipulated as such, hence expressions must be enclosed in a string literal. They are required to be valid Java code, nonetheless (with a few extensions; more on that later).

There is more we can express with just this simple information, however. Disallowing popping the stack when there are no elements in it seems like a sound contract. Since it is a requirement on the state of the object prior to the invocation of a method, it should be a method precondition; preconditions are denoted by the com.google.java.contract.Requires annotation.

```
import com.google.java.contract.Invariant;
import com.google.java.contract.Requires;

@Invariant("size() >= 0")
interface Stack<T> {
  public int size();

  @Requires("size() >= 1")
  public T pop();

  public void push(T obj);
}
```

But we may also want to assert things on the state of the object after the invocation of a method. In this example, we know that once an element has been popped, the stack is one element smaller, and once an element has been pushed on it, it is one element bigger. To write this, we need a method postcondition, denoted by the com.google.java.contract.Ensures annotation:

```
import com.google.java.contract.Ensures;
import com.google.java.contract.Invariant;
import com.google.java.contract.Requires;

@Invariant("size() >= 0")
interface Stack<T> {
  public int size();

  @Requires("size() >= 1")
  @Ensures("size() == old(size()) - 1")
  public T pop();

  @Ensures("size() == old(size()) + 1")
  public void push(T obj);
}
```

This iteration introduces two new elements: the `Ensures` annotation and the `old` keyword. The `Ensures` annotation simply delimits the postcondition, as we have seen before with `Requires` and `Invariant`. The `old` keyword inside the postcondition expression is one of the few extensions to the Java syntax supported by our framework.

The keyword `old` lets you examine old values, that is, values of expressions as they were prior to the invocation of the method. A call to this pseudo-method returns the value of its expression argument as it were before the execution of the current method.

This is about as far as we go, though, with only the size of the stack as a retrievable information. With only this description, a simple counter would be able to fool our contracts. But since the purpose of design by contract is not complete specification of your programs, you might or might not want to stop here, or even at one of the steps before. For the purpose of demonstrating more features of our cool framework, though, let's add another query method. :-)

Imagine you want stronger contracts on your stack, contracts that not only involve the size of the collection but also its contents. You could introduce a query method that returns a full copy of the stack contents as a list, but remember that this might or might not be convenient for classes implementing this interface. Here, we choose to add a lighter peek method, which returns the topmost object without popping it. Its requirements are the same as those of pop.

```
import com.google.java.contract.Ensures;
import com.google.java.contract.Invariant;
import com.google.java.contract.Requires;

@Invariant("size() >= 0")
interface Stack<T> {
  public int size();

  @Requires("size() >= 1")
  public T peek();

  @Requires("size() >= 1")
  @Ensures("size() == old(size()) - 1")
  public T pop();

  @Ensures("size() == old(size()) + 1")
  public void push(T obj);
}
```
Using `peek`, we can establish that an object popped from the stack must be the same as the one which was previously on top and, similarly, that an object just pushed onto the stack is the new topmost element. Our interface becomes:

```
import com.google.java.contract.Ensures;
import com.google.java.contract.Invariant;
import com.google.java.contract.Requires;

@Invariant("size() >= 0")
interface Stack<T> {
  public int size();

  @Requires("size() >= 1")
  public T peek();

  @Requires("size() >= 1")
  @Ensures({
    "size() == old(size()) - 1",
    "result == old(peek())"
  })
  public T pop();

  @Ensures({
    "size() == old(size()) + 1",
    "peek() == old(obj)"
  })
  public void push(T obj);
}
```

This change illustrates how multiple clauses can be fitted into a single postcondition. The same syntax can, of course, be used for invariants and preconditions. The different clauses are anded together. It also features the reserved variable result, which holds the value returned by the contracted method; it is, of course, only accessible in postconditions and only if the method returns something (that is, not void).

The resulting interface now pretty much uses all contract constructs available to a single class. But there is more to contracts than just invariants, preconditions and postconditions on a single class! Let's continue our tour by implementing a concrete stack.

# A contracted stack implementation #

Let's take a look at a simple implementation that consists of a wrapper around an ArrayList object:

```
import com.google.java.contract.Ensures;
import com.google.java.contract.Invariant;

import java.util.ArrayList;

@Invariant("elements != null")
public class ArrayListStack<T> implements Stack<T> {
  protected ArrayList<T> elements;

  public ArrayListStack() {
    elements = new ArrayList<T>();
  }

  public int size() {
    return elements.size();
  }

  public T peek() {
    return elements.get(elements.size() - 1);
  }

  public T pop() {
    return elements.remove(elements.size() - 1);
  }

  @Ensures("elements.contains(old(obj))")
  public void push(T obj) {
    elements.add(obj);
  }
}
```

This class has nearly no contracts of its own, but inherits all contracts from the `Stack` interface it implements. A contracted type inherits all the contracts from all its parents (superclasses and implemented interfaces).

Contract inheritance lets you share and mix contracts. It can be seen as an extension to the interface mechanism (though it works on superclasses too) of the core Java language, adding arbitrary assertions to their specification power.

Furthermore, contracts can be refined. If we go back to our example, we can see that an invariant and a postcondition have been added. Contrary to method overriding, these new contracts do not replace the ones inherited through `Stack`. Instead, they are combined according to the following table:

|Contract type	|Combination|
|:-------------|:----------|
|Invariants	|anded|
|Preconditions	|ored|
|Postconditions|	anded|

These rules derive intuitively from the fact that a submethod overriding a supermethod may accept contravariant input and produce covariant output. In other words, a method must always accept at least as many states and arguments on input as its parent (hence the contract can only loosen), and must at least guarantee as much as its parent (hence the contract can only strengthen), so that it can safely be used from a calling site that assumes the contract of its supermethod.

Another point worth mentioning is the fact that our new contracts refer to a protected field. A contract shares the same scope as the declaration of the method it applies to (that is, ignoring whatever may be declared in the body of the method), and can therefore access the same variables and call the same methods. Accessing restricted members is allowed but you should make your contracts public whenever possible, so they can serve as external documentation. Moreover, the practice of referring to restricted members is strongly discouraged inside preconditions, where it does not make much sense: if your method depends on a precise internal state in order to run, you should consider providing a way to query that state, as part of your interface.