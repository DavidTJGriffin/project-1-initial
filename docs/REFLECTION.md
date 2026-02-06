# Reflection Log

This document captures reflections on the development of 3D geometric classes in Java, focusing on design patterns, principles, and lessons learned.

# Reflection:
One particular design principle that I looked into was static factory method. Specifically the Point3D method in the Point3D class. I learned that the static factory method is a public static method that essentially returns an instance of a class. It's sort of like an alternative to a traditional constructor, and its benefit is that it gives you more control over how and when objects are instantiated. In reading through the three Java classes created, I really appreciated the Java docs notation above each method, explaining in detail why it was created, what it is used for, what its parameter is for. It gives me a really good sense into system design, and actually learned quite a bit about the static factory method and specifically how it is a better approach to the traditional constructor, which is limited to have the same name of the class - that can make code confusing. Having a static factory design pattern also provides benefits of performance as well. 
