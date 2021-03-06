jeannie - a general purpose generator for the rest of us
========================================================

* Author:    alvi
* Date:      June, 2011
* Version:   0.1.6
* Website:   <http://softbork.com/jeannie>
* GitHub:    <https://github.com/hippiefahrzeug/jeannie>

If you've come here to find your definitive solution for code generation (or any sort of generation), you've come to the right place.


jeannie features
----------------

* VERY simple to use. you can be up and running in a couple of minutes
* builds on great technologies. If you're familiar with Stringtemplate and groovy, you'll be generating in no time. Don't worry if you don't know either, you still can get started with minimal effort.
* generates anything you want, from source code to web pages to sql scripts... whatever you fancy.
* facilitates generator reuse
* facilitates module maintenance
* parses a wide range of input file types
* comes with an extensive library of processors

If the last two points doesn't make any sense, that's ok. You just need to know that while jeannie is terribly simple, it is extremely powerful.

Some words about generators
---------------------------

Whenever you're looking for a general purpose generator, you will find huge frameworks, software platforms, systems, etc. and they will all occupy you for many hours if not days to get a 'hello world' generated. These tools often have their purpose, for instance as an MDA tool where this kind of complexity is... reassuring. However, if you want to generate some smaller things (configuration files, entities, sql scripts, etc.), the effort is almost never justified to use these tools, as they are slow, big, complicated and usually no fun to use.

Because of this reason, you probably ended up creating your own ad-hoc generators in the past. They get the job done, but the problem is that such generators are often not reusable (or hard to reuse), not maintainable and only understandable by one person: you.

jeannie addresses these problems and helps reusing your generators and share them. It has a very simple concept: Everything that defines how something is generated is contained within a module. A module is a well defined collection of files (either in a jar or on the file system):

    module/                    - can be named anything
        scriptlets/            - contains your groovy scripts (optional)
        templates/             - contains the templates
        BANNER                 - this is printed out whenever this generator runs
        README                 - documentation about the module
        module-reflections.xml - speeds up class scanning

You create a template and maybe a scriptlet, and you have a working generator. A module like this can easily be versioned and retrieved from a repository. If you have a project that has its own module that proved to be useful, it can be easily extracted into a widely available module.


currently working on
--------------------

- tutorial
- giving final polish for first release
- cmdline tools

future plans
------------

* evaluate BSF

