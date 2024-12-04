import java.time.Duration;
import java.lang.String;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

class TestsPaginaPrincipal {

	private static WebDriver driver;

	@BeforeAll
	public static void setUp() {
		// configuracion de opciones del driver.
		FirefoxOptions options = new FirefoxOptions();
		options.addArguments("--headless");

		// inicializacion del driver.
		driver = new FirefoxDriver(options);

		// obtención de la página.
		driver.get("http://localhost:8080/owners/find");
	}

	@Test
	public void test_find_owner_correcto() {
		// obtención de la página.
		driver.get("http://localhost:8080/owners/find");

		// busqueda del formulario.
		WebElement formulario = driver.findElement(By.tagName("form"));

		// generamos la interacción con la el formulario.
		formulario.findElement(By.id("lastName")).sendKeys("Franklin");
		formulario.findElement(By.cssSelector("button[type='submit']")).click();

		// verificamos que se haya redirigido al nuevo link.
		assertEquals(driver.getCurrentUrl(), "http://localhost:8080/owners/1");

		// obtenemos la lista de datos del dueño al que intentamos acceder.
		List<WebElement> datos_dueno = driver
			.findElements(By.xpath("//h2[contains(text(), 'Owner Information')]/following-sibling::table/tbody/tr/td"));

		// comprobamos que la información mostrada es la que esperamos del usuario 1.
		assertEquals(datos_dueno.get(0).getText(), "George Franklin");
		assertEquals(datos_dueno.get(1).getText(), "110 W. Liberty St.");
		assertEquals(datos_dueno.get(2).getText(), "Madison");
		assertEquals(datos_dueno.get(3).getText(), "6085551023");

		// obtenemos la lista de datos de las mascotas que tiene.
		List<WebElement> datos_mascota = driver.findElements(
				By.xpath("//h2[contains(text(), 'Pets and Visits')]/following-sibling::table/tbody/tr/td[1]/dl/dd"));

		// comprobamos que la información mostrada es la que esperamos de las mascotas.
		assertEquals(datos_mascota.get(0).getText(), "Leo");
		assertEquals(datos_mascota.get(1).getText(), "2010-09-07");
		assertEquals(datos_mascota.get(2).getText(), "cat");
	}

	@Test
	public void test_find_owner_error() {
		// obtención de la página.
		driver.get("http://localhost:8080/owners/find");

		// busqueda del formulario.
		WebElement formulario = driver.findElement(By.tagName("form"));

		// generamos la interacción con la el formulario.
		formulario.findElement(By.id("lastName")).sendKeys("asodopaosdnpasjebrwieproqwie");
		formulario.findElement(By.cssSelector("button[type='submit']")).click();

		// esperamos a que se genere el error en la interfaz.
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
		WebElement elem = wait.until(ExpectedConditions.elementToBeClickable(By.className("help-inline")));

		// comprobamos que el mensaje de error sea el que esperamos.
		assertEquals(elem.getText(), "has not been found");
	}

	@Test
	public void test_new_pet() {
		String name = "Gladiator";
		String date = "2016-11-10";
		String type = "cat";

		// obtención de la página.
		driver.get("http://localhost:8080/owners/1/pets/new");

		// obtengo el formulario.
		WebElement formulario = driver.findElement(By.tagName("form"));

		// interactuamos con los elementos del formulario.
		formulario.findElement(By.tagName("input").id("name")).sendKeys(name);
		formulario.findElement(By.tagName("input").id("birthDate")).sendKeys(date);
		(new Select(formulario.findElement(By.tagName("select").id("type")))).selectByValue(type);
		formulario.findElement(By.cssSelector("button[type='submit']")).click();

		// comprobamos que se haya redirigido a la nueva página.
		assertTrue(driver.getCurrentUrl().contains("http://localhost:8080/owners/1"));

		// obtenemos la lista de datos de las mascotas que tiene.
		List<WebElement> datos_mascota = driver.findElements(
				By.xpath("//h2[contains(text(), 'Pets and Visits')]/following-sibling::table/tbody/tr/td[1]/dl/dd"));

		// comprobamos que la información mostrada es la que esperamos de las mascotas.
		assertEquals(datos_mascota.get(0).getText(), name);
		assertEquals(datos_mascota.get(1).getText(), date);
		assertEquals(datos_mascota.get(2).getText(), type);

		assertEquals(datos_mascota.get(3).getText(), "Leo");
		assertEquals(datos_mascota.get(4).getText(), "2010-09-07");
		assertEquals(datos_mascota.get(5).getText(), "cat");
	}

	@AfterAll
	public static void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

}
