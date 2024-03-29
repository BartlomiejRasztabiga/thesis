# Chapter 1: Event Storming

## 1.1 Introduction

In order to understand the processes and identify potential microservices in the developed application, an Event Storming session was conducted. This method allows for the visualization and modeling of business processes within the system, which is crucial when creating complex IT systems. In this chapter, the results of the Event Storming session will be presented, which was conducted using the Miro tool.

## 1.2 Miro Tool

Miro is an interactive collaboration platform that allows for the creation of boards with notes, drawings, and diagrams. The choice of this tool was dictated by its ease of use and visualization capabilities related to Event Storming.

## 1.3 Identification of Bounded Contexts

As a result of the Event Storming session, five Bounded Contexts were identified, which will be key
to the developed application. These are:

1. Restaurant - covers the management of restaurants, menus, and product availability.
2. Order - responsible for the order process, such as placing, modifying, and canceling orders.
3. Delivery - focuses on delivery logistics, monitoring delivery status, and communication with the supplier. Also manages the supplier database.
4. Payment - manages the payment process, includes various payment methods and transaction handling. Handles all settlements within the system.
5. User - concerns the management of user accounts, authentication, and permission management.
6. Invoice - responsible for generating invoices.

# Process Analysis

## 1.4 Process Analysis

Each of the aforementioned boundary contexts has undergone an analysis of the processes that occur within the given domain. During the analysis, the following key events were identified:

1. **Restaurant:**
    - Adding a new restaurant
    - Updating restaurant information
    - Adding/updating menu
    - Changing restaurant availability
    - Accepting or rejecting an order by the restaurant
    - Updating order status (ready for pickup)

2. **Order:**
    - Creating a new order
    - Modifying an order
    - Cancelling an order
    - Finalizing an order

3. **Delivery:**
    - Adding a new delivery provider
    - Updating delivery provider information
    - Changing delivery availability
    - Assigning a delivery provider to an order
    - Mapping delivery route
    - Pickup of order by the delivery provider
    - Delivery of order to the customer

4. **Payment:**
    - Accepting payment for an order
    - Handling unsuccessful payments
    - Settling payments with the delivery provider
    - Settling payments with the restaurant
    - Processing payment refunds in the event of order cancellation
    - Payout to the restaurant
    - Payout to the delivery provider

5. **User:**
    - Registering a new user
    - User login
    - Updating user data (payment methods, delivery addresses, preferences)
    - Deleting a user account

6. **Invoice:**
    - Generating an invoice for the restaurant
    - Generating an invoice for the delivery provider
    - Generating an invoice for the ordering party

## 1.5 Graphical Representation

An Event Storming diagram is included as a visual representation of the analysis of the key events within each boundary context. The diagram can be found in the file "event_storming.png".


