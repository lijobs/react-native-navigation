package com.reactnativenavigation.viewcontrollers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.reactnativenavigation.parse.NavigationOptions;

public class ContainerViewController extends ViewController {

	public interface ContainerViewCreator {

		ContainerView create(Activity activity, String containerId, String containerName);
	}

	public interface ContainerView {

		boolean isReady();

		View asView();

		void destroy();

		void sendContainerStart();

		void sendContainerStop();

	}

	private final String containerName;

	private final ContainerViewCreator viewCreator;
	private final NavigationOptions navigationOptions;
	private ContainerView containerView;

	public ContainerViewController(final Activity activity,
	                               final String id,
	                               final String containerName,
	                               final ContainerViewCreator viewCreator,
	                               final NavigationOptions initialNavigationOptions) {
		super(activity, id);
		this.containerName = containerName;
		this.viewCreator = viewCreator;
		this.navigationOptions = initialNavigationOptions;
	}

	@Override
	public void destroy() {
		super.destroy();
		if (containerView != null) containerView.destroy();
		containerView = null;
	}

	@Override
	public void onViewAppeared() {
		super.onViewAppeared();
		containerView.sendContainerStart();
	}

	@Override
	public void onViewDisappear() {
		super.onViewDisappear();
		containerView.sendContainerStop();
	}

	@Override
	protected boolean isViewShown() {
		return super.isViewShown() && containerView.isReady();
	}

	@NonNull
	@Override
	protected View createView() {
		containerView = viewCreator.create(getActivity(), getId(), containerName);
		return containerView.asView();
	}

	public void applyNavigationOptions(final NavigationOptions options) {
		navigationOptions.mergeWith(options);
		if (getParentStackController() != null) {
			getParentStackController().setTitle(navigationOptions.title);
		}
	}

	public NavigationOptions getNavigationOptions() {
		return navigationOptions;
	}
}