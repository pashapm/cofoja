# Should a stack be liberal or conservative? #

Contracts state assumptions that must hold for your software to run correctly. They make explicit what otherwise is implicitly encoded in your system. Contracts are not meant to define behavior, instead they describe behavior. Without contracts these assumptions are usually stated as comments.

Let's take a stack class as an example. How should its contract look like? Should you put a precondition on `pop` that forbids its invocation if the stack is empty? Well that depends. In the end it is a design question. Ask yourself: should clients be allowed to call `pop` on an empty stack?  You can use contracts to make the answer to this question explicit. If you decide that clients must not call `pop` on an empty stack the stack class might look a bit like this one:

```
class ConservativeStack<T> {
  public boolean isEmpty() { ... }

  @Requires("!isEmpty()")
  public T pop() { ... }
}
```

If you decide that clients may call `pop` on an empty stack and `pop` should return `null` the stack class might look like this instead:

```
class LiberalStack<T> {
  public boolean isEmpty() {...}

  @Ensures("isEmpty() => result == null")
  public T pop() { ... }
}
```

If clients may call `pop` on an empty stack, but `pop` should throw an exception the stack class might look like this:

```
class LiberalStack<T> {
  public boolean isEmpty() {...}

  @ThrowEnsures({"SomeException", "isEmpty()"})
  public T pop() { ... }
}
```

Note that a client of class `ConservativeStack` must not call `pop` on an empty stack. The behavior of `pop` on an empty `ConservativeStack` is undefined in general. Only when you enable runtime checking of contracts you get defined behavior. But you must not rely on this, since for runtime checking usually is disabled in for production binaries. Don't write code like this:

```
ConservativeStack s = ...
try {
    s.pop();
} catch (PreconditionError e) {
    // Do stuff in case the stack is empty.
}
```

Instead write:

```
ConservativeStack s = ...
if (s.isEmpty()) {
    // Do stuff in case the stack is empty.
} else {
    s.pop();
}
```

# Users don't like contracts #

Developers who start using contracts are sometimes tempted to use contracts to handle invalid user input (or invalid file i/o, or any other external input). Contracts are meant to state assumptions that are required for your program to work correctly. Users do make mistakes.  And you certainly want your program to run correctly when a user makes a mistake. So don't use contracts to handle invalid input.

What should you do instead? Ideally you separate the GUI code from your model and let the GUI code do the input checking. For example, let's say you want the user to give you the name of a person, but the name must not be null or empty. You'd have a class `Person` with a field `name`. That's your model. And then you have a GUI class representing the dialog, let's say `NameQueryingDialog`. The two classes could look a bit like this:

```
@Invariant({
  "name != null", 
  "!name.isEmpty()"
})
class Person {    
  private String name;

  @Ensures("result == name")
  public String getName() {...}

  @Requires({
    "newName != null",
    "!newName.isEmpty()"
  })
  @Ensures("getName() == newName")
  public void setName(String newName) {...}
}

class NameQueryingDialog {
  ...

  // Below method is registered as a callback with the GUI toolkit. Whenever
  // the user enters a name, the GUI toolkit invokes this method.
  // We assume that the GUI toolkit never calls this method with a null argument
  @Requires("s != null")
  public void receiveInput(String s) {
    if (s.isEmpty()) {
      // Tell the user that he entered invalid input
    } else {
      person.setName(s)
    }
  }
}
```

There are plenty of contracted classes in Contracts for Java that you can look at to get a feeling for how to write good contracts.

The following principles and guidelines are adopted from "Design by Contract, by Example", Richard Mitchell and Jim McKim. It is an excellent book on how to write contracts. There is a sample chapter online that might help you understand the below principles better.Thanks to Benjamin Manes for reminding us about it.

## Principles ##

  * Separate methods into commands and queries.
    * A query is a method that returns something (i.e. non-void). It should be used to ask the object a question and asking a question. Since asking a question twice should not change its answer queries should not change the visible properties of its object.
    * A command is a method that returns nothing (i.e. void).
    * Following this principle is important because it allows you to use queries in assertions without fearing that they might change state.
  * Separate basic queries from derived queries.
    * Derived queries can be specified in terms of basic queries.
  * For each derived query, write a postcondition that specifies what result will be returned in terms of one or more basic queries.
    * Then, if we know the values of the basic queries, we also know the value of the derived queries.
  * For every query and command, decide on a suitable precondition.
    * Preconditions constrain when clients may call the queries and commands.
  * Write invariants to define unchanging properties of objects.
    * Concentrate on properties that help the reader build an appropriate conceptual model of the abstraction that the class embodies.
  * For each command, write a postcondition that specifies the value of every basic query.
    * Taken together with the principle of defining derived queries in terms of basic queries, this means that we now know the total visible effect of every command.
    * (In general it is difficult to specify what doesn't change. Following this recommendation leads to very verbose post-conditions. Apply as you see fit.)

## Guidelines ##

  * Add physical constraints where appropriate.
    * Typically, these will be constraints that variables should not be null.
  * Make sure that queries used in preconditions are cheap to calculate.
    * If necessary, add cheap-to-calculate derived queries whose post-conditions verify them against more expensive queries.
  * Constrain fields using an invariant
    * When a derived query is implemented as an field, it can be constrained to be consistent with other queries by an assertion in the class's invariant section.

## Other resources ##

There is an introductory presentation to Design By Contract on <a href='http://www.eiffel.com'>eiffel.com</a> that is worth watching.