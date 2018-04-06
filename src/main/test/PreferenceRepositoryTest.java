package main.test;

import org.junit.Test;

import org.junit.Assert;
import main.PreferenceRepository;

public class PreferenceRepositoryTest {
	
	private PreferenceRepository getPr() {
		PreferenceRepository pr = new PreferenceRepository(
				"C:\\Users\\kaamr\\workspace\\UVSmartApp\\Resources\\preferences.txt");
		return pr;
	}
	
	@Test
	public void getUserPrefTest() {
		PreferenceRepository pr = getPr();
		String AliceUVExp = "pool";
		String AliceUVTest = pr.getUserPreference("Alice", "UVO");
		String AliceTempExp = "cinema";
		String AliceTempTest = pr.getUserPreference("Alice", "38");
		//illegal
		String test = pr.getUserPreference("Alice", "MV");
		Assert.assertEquals(AliceTempExp, AliceTempTest);
		Assert.assertEquals(AliceUVTest, AliceUVExp);
		Assert.assertNull(test);
	}
}
