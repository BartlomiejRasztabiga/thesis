# Chapter 6: Non-Functional Requirements

This chapter presents detailed non-functional requirements of the application.

## 6.1. List of Non-Functional Requirements

- [ ] Scalability - each component of the system should be scalable based on the load. For example, each microservice
  should be able to be replicated.
- [ ] High Availability - if one microservice is unavailable, other microservices should continue to function properly.
- [ ] Security - the application should provide appropriate security measures to protect the confidentiality, integrity,
  and availability of data. For example, access to microservices should be protected by authentication and
  authorization, and data should be encrypted in transit.
- [ ] Monitoring and Logging - the application should enable event monitoring and logging for analysis and debugging
  purposes. For example, each microservice should log its events in a central repository, and the application should
  allow monitoring the state of microservices.
- [ ] Performance - the system should be capable of processing a minimum of 10 requests per second.
- [ ] Data Synchronization and Consistency - the system should ensure data synchronization and consistency across
  microservices.
- [ ] Testability - the system should be easy to test, both at the unit and integration levels.
