package fr.univmobile.backend.core;

@Category("regions")
@PrimaryKey("uid")
@Support(data = Region.class, builder = RegionBuilder.class)
public interface RegionDataSource extends
		BackendDataSource<Region, RegionBuilder> {

	@SearchAttribute("uid")
	Region getByUid(String uid);

	boolean isNullByUid(String uid);
}
