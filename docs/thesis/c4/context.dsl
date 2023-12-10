workspace {

    model {
        user = person "Ordering user"
        restaurant = person "Restaurant manager"
        courier = person "Courier"
        
        softwareSystem = softwareSystem "System" "Food delivery system"
        
        googleMapsPlatform = softwareSystem "Google Maps Platform"
        auth0 = softwareSystem "Auth0"
        stripe = softwaresystem "Stripe"
        mailjet = softwareSystem "Mailjet"
        invoiceGenerator = softwaresystem "Invoice Generator"

        user -> softwareSystem "Uses"
        restaurant -> softwareSystem "Uses"
        courier -> softwareSystem "Uses"
        
        softwareSystem -> googleMapsPlatform "Calculates distance, delivery route"
        softwareSystem -> auth0 "Authenticates users"
        softwareSystem -> stripe "Processes payments"
        softwareSystem -> mailjet "Sends e-mails"
        softwareSystem -> invoiceGenerator "Generates invoices"
        
    }

    views {
        systemContext softwareSystem "SystemContext" {
            include *
            autoLayout
        }

        styles {
            element "Software System" {
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