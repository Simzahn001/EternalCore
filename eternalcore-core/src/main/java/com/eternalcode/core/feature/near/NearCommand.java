package.eternalcore.core.feature.near;

import com.eternalcode.core.notice.NoticeService;
import com.eternalcode.commons.bukkit.scheduler.MinecraftScheduler;

import java.util.Optional;

@Command(name = "near", aliases = {})
@Permission("eternalcore.feature.near")
public class NearCommand {

    private final NoticeService noticeService;
    private final MinecraftScheduler minecraftScheduler;
    private static final int RADIUS = 100;
    private static final long GLOW_TIME = 5L;

    @Inject
    public NearCommand(NoticeService noticeService, minecraftScheduler minecraftScheduler) {
        this.minecraftScheduler = minecraftScheduler;
        this.noticeService = noticeService;
    }

    @Execute
    @DescriptionDocs(description = "Show all nearby entities within a default radius.", arguments = "[]")
    void showEntites(@Context Player sender) {
        this.handleShowEntites(sender, RADIUS);
    }

    @Execute
    @DescriptionDocs(description = "Show all nearby entities within a specified radius.", arguments = "[radius]")
    void showEntitiesInRadius(@Context Player sender, @Arg Optional<Integer> radius) {
        int actualRadius = radius.orElse(RADIUS);

        if (actualRadius <= 0) {
            this.noticeService.create()
                .player(sender.getUniqueId())
                .notice(translation -> translation.argument().numberBiggerThanZero())
                .send();
            return;
        }

        handleShowEntites(sender, actualRadius);
    }

    private void handleShowEntites(Player sender, int radius) {

        Collection<Entity> nearbyEntities = sender.getNearbyEntities(radius, radius, radius).stream()
            .filter(entity -> entity.getUniqueId() != sender.getUniqueId())
            .collect(Collectors.toList());

        if (nearbyEntities.isEmpty()) {
            this.noticeService.create()
                .player(sender.getUniqueId())
                .placeholder("{RADIUS}", String.valueOf(actualRadius))
                .notice(translation -> translation.near().noEntitiesFound())
                .send();
            return;
        }

        nearbyEntities.foreach(entity -> entity.setGlowing(true));
        this.minecraftScheduler.runTaskLater(
            () -> {nearbyEntities.forEach(entity -> entity.setGlowing(false));},
            GLOW_TIME * 20 // Seconds to Ticks
        );

        Map<EntityType, Integer> entityTypeCount = nearbyEntities.stream()
            .collect(Collectors.groupingBy(
                Entity::getType,
                Collectors.counting()
            ));

        StringBuilder entityList = new StringBuilder();
        entityTypeCount.forEach((type, count) -> {
            entityList.append("  ").append(type.name()).append(": ").append(count).append("<br>");
        }

        this.noticeService.create()
            .player(sender.getUniqueId())
            .placeholder("{ENTITESAMOUNT}", nearbyEntities.length())
            .placeholder("{RADIUS}", String.valueOf(radius))
            .placeholder("{ENTITYLIST}", entityList.toString())
            .notice(translation -> translation.near().foundEntities())
            .send();
    }

}
