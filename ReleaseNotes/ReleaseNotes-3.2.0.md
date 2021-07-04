### Added Interfaces ###
* `org.lightsleep.entity.PostDelete`
* `org.lightsleep.entity.PostInsert`
* `org.lightsleep.entity.PostSelect`
* `org.lightsleep.entity.PostUpdate`
* `org.lightsleep.entity.PreDelete`
* `org.lightsleep.entity.PreUpdate`

### Added Classes ###
* `org.lightsleep.database.MariaDB`
* `org.lightsleep.database.anchor.mariadb`

### Specification Changes ###
* Changed the return type of `preInsert` method of the `PreInsert` interface from `int` to `void`.
* Changed `org.lightsleep.database.DB2` class to `Db2`.

### Deprecated Interfaces ###
* `org.lightsleep.entity.Composite`
* `org.lightsleep.entity.PostLoad`
* `org.lightsleep.entity.PreStore`
