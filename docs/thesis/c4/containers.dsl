workspace {

    model {
        user = person "Ordering user"
        restaurantManager = person "Restaurant manager"
        courier = person "Courier"
        
        foodDeliverySystem = softwaresystem "Food delivery system" {
            singlePageApplication = container "Single-Page Application"

            delivery = container "Delivery service"
            order = container "Order service"
            payment = container "Payment service"
            restaurant = container "Restaurant service"
            
            query = container "Query service"
            queryDb = container  "Query service DB" "Query service DB"
            saga = container "Saga service"
            sagaDb = container "Saga service DB"
        }
        
        googleMapsPlatform = softwareSystem "Google Maps Platform"
        auth0 = softwareSystem "Auth0"
        stripe = softwaresystem "Stripe"
        mailjet = softwareSystem "Mailjet"
        invoiceGenerator = softwaresystem "Invoice Generator"
        
        user -> singlePageApplication "Uses"
        restaurantManager -> singlePageApplication "Uses"
        courier -> singlePageApplication "Uses"
        
        singlePageApplication -> delivery "Sends commands"
        singlePageApplication -> order "Sends commands"
        singlePageApplication -> payment "Sends commands"
        singlePageApplication -> restaurant "Sends commands"
        
        singlePageApplication -> query "Queries data"
        
        delivery -> saga "Uses sagas"
        order -> saga "Uses sagas"
        payment -> saga "Uses sagas"
        restaurant -> saga "Uses sagas"
        
        query -> queryDb "Uses"
        saga -> sagaDb "Uses"
        
        delivery -> googleMapsPlatform "Calculates distance, delivery route"
        foodDeliverySystem -> auth0 "Authenticates users"
        payment -> stripe "Processes payments"
        payment -> mailjet "Sends e-mails"
        payment -> invoiceGenerator "Generates invoices"
        
    }

    views {
        systemContext foodDeliverySystem "SystemContext" {
            include *
            autoLayout
        }
        
        container foodDeliverySystem "Containers" {
            include *
            autoLayout
        }

        styles {
            element "Container" {
                background #1168bd
                color #ffffff
            }
            element "Person" {
                shape person
                background #08427b
                color #ffffff
            }
        }
    }
    
}