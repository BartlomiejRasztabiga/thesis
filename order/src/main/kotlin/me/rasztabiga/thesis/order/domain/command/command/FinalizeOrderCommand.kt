package me.rasztabiga.thesis.order.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class FinalizeOrderCommand(
    @TargetAggregateIdentifier val orderId: UUID,
    val userId: String,
    val deliveryAddressId: UUID
)


// Interceptor:
// 1. get order by id
// 2. verify that restaurant is still open (?)
// 3. verify that products exist
// 4. verify that user exists

// Saga:
// 1. order finalization started (event)
// 2. calculate order total (command)
// 3. order total calculated (event)
// 4. create order payment (command)
// 5. order payment created (event)
// 6. pay order payment (command, caused by REST payment for now)
// 7. order payment paid (event)
// 8. mark order as paid (command)
// 9. order marked as paid (event)
// 10. create restaurant order (command)
// 11. restaurant order created (event)
// 12. mark order as confirmed by the restaurant (command)
// 13. order marked as confirmed by the restaurant (event)
// 14. mark order as prepared (command)
// 15. order marked as prepared (event)
// 16. find courier (command)
// 17. courier accepted (event)
// 18. pick up order delivery (command)
// 19. order picked up (event)
// 20. deliver order (command)
// 21. order delivered (event)

// payment can also timeout (deadline)
// payment can also fail (how?)

// TODO co z tego robic w interceptorze a co w sadze? jak dlugo ma saga zyc?
