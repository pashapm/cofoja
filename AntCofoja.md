Ant is a widely used, handy way of managing your project's build process. This page shows the basics to integrate Contracts for Java with Ant build files.

## Add cofoja to the classpath ##

As any library, you must add Contracts for Java to your classpath. A simple way of doing this is using the classpath attribute of the javac task.

## The annotation processor ##

Cofoja uses a JSR 269 annotation processor to extract contract information at compile time. Simply adding cofoja to the classpath should be enough to have your annotations processed.

Since cofoja is an annotation processor and it runs before the actual compilation, normal errors that would be catched by the compiler may be reported by cofoja in... cryptic ways.
Also, as cofoja only processes the subset of your project that effectively uses contracts, some references may be missing during contract compilation.
Nhat provided a nice explanation of this in [Issue 20](https://code.google.com/p/cofoja/issues/detail?id=20) (see comment 13).

A good practice that bypasses these issues is compiling contracts in a _separate step_, right after the normal compilation. For this you can use the javac options `-proc:none` and `-proc:only`. The first pass will produce normal class files while the second pass will yield contracts files; for this to work, you need to include the class files output of the first invocation in the second's class path so Cofoja can find any class from your project.

Also, remember to specify different output directories to each task, otherwise ant will not execute the second one.

Example of how to do this:

```
  <target name="build">
    <mkdir dir="${build.dir}"/>
    
    <!-- Compiles WITHOUT contracts. -->
    <mkdir dir="${classes.dir}"/>
    <javac srcdir="${src.dir}" destdir="${classes.dir}">
      <classpath refid="class.path"/>
      <compilerarg value="-proc:none"/>
    </javac>

    <!-- Compiles ONLY the contracts. -->
    <mkdir dir="${contracts.dir}"/>
    <javac srcdir="${src.dir}" destdir="${contracts.dir}">
      <classpath>
        <path refid="class.path"/>
        <!-- Add ${classes.dir} to the second compilation class path
             so that Cofoja finds your already compiled class files. -->
        <pathelement path="${classes.dir}"/>
      </classpath>
      <compilerarg value="-proc:only"/>
    </javac>
  </target>
```

## Java agent ##

Cofoja uses a java agent to instrument bytecode and weave the contract checks into your code during runtime.

Simply add the cofoja agent like this:

```
  <target name="run" depends="dist">
    <java jar="${dist.dir}/${ant.project.name}.jar" fork="true">
      <jvmarg value="-javaagent:${lib.dir}/${cofoja}"/>
    </java>
  </target>
```

# Full example #

Here's the full Ant buildfile that you can use as base to your project.

```
<project name="CofojAnt" default="run" basedir=".">

  <!-- Project directories. -->
  <property name="src.dir" location="src"/>
  <property name="dist.dir" location="dist"/>
  <property name="lib.dir" location="lib"/>
  <property name="build.dir" location="build"/>
  <property name="classes.dir" location="${build.dir}/classes"/>
  <property name="contracts.dir" location="${build.dir}/contracts"/>

  <property name="main-class"
    value="com.google.java.contract.example.CofojAnt"/>

  <property name="cofoja" value="cofoja.jar"/>

   <path id="class.path">
     <pathelement location="${lib.dir}/${cofoja}"/>
   </path>

  <target name="build">
    <mkdir dir="${build.dir}"/>
    
    <!-- Compiles WITHOUT contracts. -->
    <mkdir dir="${classes.dir}"/>
    <javac srcdir="${src.dir}" destdir="${classes.dir}">
      <classpath refid="class.path"/>
      <compilerarg value="-proc:none"/>
    </javac>

    <!-- Compiles ONLY the contracts. -->
    <mkdir dir="${contracts.dir}"/>
    <javac srcdir="${src.dir}" destdir="${contracts.dir}">
      <classpath>
        <path refid="class.path"/>
        <!-- Add ${classes.dir} to the second compilation class path
             so that Cofoja finds your already compiled class files. -->
        <pathelement path="${classes.dir}"/>
      </classpath>
      <compilerarg value="-proc:only"/>
    </javac>
  </target>

  <target name="dist" depends="build">
    <mkdir dir="${dist.dir}"/>
    <jar destfile="${dist.dir}/${ant.project.name}.jar">
      <fileset dir="${classes.dir}"/>
      <fileset dir="${contracts.dir}"/>
      <manifest>
        <attribute name="Main-Class" value="${main-class}"/>
      </manifest>
    </jar>
  </target>

  <target name="run" depends="dist">
    <java jar="${dist.dir}/${ant.project.name}.jar" fork="true">
      <jvmarg value="-javaagent:${lib.dir}/${cofoja}"/>
    </java>
  </target>

  <target name="clean">
    <delete dir="${build.dir}"/>
    <delete dir="${dist.dir}"/>
  </target>
</project>
```