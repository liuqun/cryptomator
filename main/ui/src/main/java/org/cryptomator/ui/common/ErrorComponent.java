package org.cryptomator.ui.common;

import com.google.common.base.Strings;
import dagger.BindsInstance;
import dagger.Subcomponent;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

@Subcomponent(modules = {ErrorModule.class})
public interface ErrorComponent {

	Throwable cause();

	Stage window();

	@Nullable
	String message();

	@Nullable
	Boolean suppressErrorLogging();

	@FxmlScene(FxmlFile.ERROR)
	Scene scene();

	default void reportToUser() {
		Logger logger = LoggerFactory.getLogger(ErrorComponent.class); //TODO Caller Sensitive?
		String message = Strings.isNullOrEmpty(message()) ? "An unexpected error occured" : message();

		Boolean suppressErrorLogging = suppressErrorLogging();
		if (suppressErrorLogging == null || !suppressErrorLogging) {
			logger.error(message, cause());
		} else {
			logger.error(message);
		}
		showOnFXThread();
	}

	default void showErrorScene() {
		if (!Platform.isFxApplicationThread()) {
			throw new IllegalStateException("Must be run on FX Application Thread");
		}
		Stage stage = window();
		stage.setScene(scene());
		stage.show();
	}

	default void showOnFXThread() {
		Platform.runLater(this::showErrorScene);
	}

	@Subcomponent.Builder
	interface Builder {

		@BindsInstance
		Builder cause(Throwable cause);

		@BindsInstance
		Builder window(Stage window);

		@BindsInstance
		Builder returnToScene(@Nullable Scene previousScene);

		@BindsInstance
		Builder message(@Nullable String message);

		@BindsInstance
		Builder suppressErrorLogging(@Nullable Boolean suppressErrorLogging);

		ErrorComponent build();

	}

}
