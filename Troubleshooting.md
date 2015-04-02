Sometimes configuration mistakes can lead to cryptic errors. This page should concentrate solutions for common problems you may have.

Of course if the proposed diagnostic/solution does not fit your case, feel free to fill a bug.

### Class circularity error ###

You're probably **instrumenting twice**. In other words, you may be passing the JVM flag with the Cofoja javaagent more than once.

### NPE on ASM ###

Getting a NPE at org.objectweb.asm.commons.GeneratorAdapter.catchException(Unknown
Source)
???

You're probably using an **old version of ASM**. Cofoja requires **ASM 3.2** or higher to run.