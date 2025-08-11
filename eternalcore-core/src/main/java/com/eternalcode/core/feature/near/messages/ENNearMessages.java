package.eternalcore.core.feature.near.messages;

@Getter
@Accessors(fluent = true)
public class ENNearMessages extends OkaeriConfig implements NearMessages {

    @Comment("# Available placeholders: {RADIUS} - the radius within which entities were searched")
    public Notice noEntitiesFound = Notice.chat("<red>► <white>No entities found within a radius of {RADIUS} blocks. Please try again with a different radius.");

    @Comment("# Available placeholders: {ENTITYAMOUNT} - the amount of entities found and shown, {RADIUS} - the radius within which entities were searched, {ENTITYLIST} - the list of all entities found grouped by the entity type")
    public Notice entitiesShown = Notice.chat("<green>► <white>{ENTITYAMOUNT} entities found and shown within a radius of {RADIUS} blocks: <br> {ENTITYLIST}");
}
