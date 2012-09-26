Why we created phoenix?
=======================

Goal
----
* Enable core & third-party java libraries (which java apps depends on) to update independently.

Problem
-------
The classical packaging & deployment strategy looks like this: 

![png](https://raw.github.com/dianping/phoenix/master/docs/images/why-1.png)

If we update framework library from 1.0 to 1.1 for one app:

![png](https://raw.github.com/dianping/phoenix/master/docs/images/why-2.png)

If we update this for multiple apps (eg.100+ apps), what will happen?  

**THIS GONNA BE A DISASTER**

HOWEVER, this happens again and again...  

* When a critical bug of the framework library is fixed
* When a new feature of the framework library is announced
* When an important update of third-party library is published

New Idea
--------
* Exclude framework & third-party libraries from app.war
* Customize web container to load both libraries and app war.

![png](https://raw.github.com/dianping/phoenix/master/docs/images/why-3.png)


If we update framework library from 1.0 to 1.1 for multiple apps:  
(Prerequisites: no change with framework library API)


![png](https://raw.github.com/dianping/phoenix/master/docs/images/why-4.png)


