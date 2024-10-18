package com.elice.holo.category.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategory is a Querydsl query type for Category
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategory extends EntityPathBase<Category> {

    private static final long serialVersionUID = 1735128293L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategory category = new QCategory("category");

    public final com.elice.holo.common.QBaseEntity _super = new com.elice.holo.common.QBaseEntity(this);

    public final NumberPath<Long> categoryId = createNumber("categoryId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final QCategory parentCategory;

    public final ListPath<Category, QCategory> subCategories = this.<Category, QCategory>createList("subCategories", Category.class, QCategory.class, PathInits.DIRECT2);

    public QCategory(String variable) {
        this(Category.class, forVariable(variable), INITS);
    }

    public QCategory(Path<? extends Category> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategory(PathMetadata metadata, PathInits inits) {
        this(Category.class, metadata, inits);
    }

    public QCategory(Class<? extends Category> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parentCategory = inits.isInitialized("parentCategory") ? new QCategory(forProperty("parentCategory"), inits.get("parentCategory")) : null;
    }

}

