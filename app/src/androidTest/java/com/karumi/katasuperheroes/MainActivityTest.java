/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.recyclerview.RecyclerViewInteraction;
import com.karumi.katasuperheroes.ui.view.MainActivity;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

  private static final int NUMBER_SUPER_HEROES = 10;

  @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @Test public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
  }

  // Si hay super heroe al abrir la app no se muestra el emptycase
  @Test public void doesNotShowsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreSomeSuperHeroes(10, false);

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(not(isDisplayed())));
  }

  private void givenThereAreNoSuperHeroes() {
    when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
  }

  // Si hay un SH cuando abro la app que muestre el nombre
  @Test public void ShowsNameIfThereAreSuperHeroes() {
    List<SuperHero> superHeroes = givenThereAreSomeSuperHeroes(1, false);

    startActivity();

    onView(withText(superHeroes.get(0).getName())).check(matches((isDisplayed())));
  }

  //Si hay 10 superheroes cuando abro la app se muestran sus 10 nombres
  @Test public void showsSuperHeroesNameIfThereAreSuperHeroes() {
    List<SuperHero> misSuperHeroes = givenThereAreSomeSuperHeroes(NUMBER_SUPER_HEROES);

    startActivity();

    RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
            .withItems(misSuperHeroes)
            .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
              @Override public void check(SuperHero superHero, View view, NoMatchingViewException e) {
                matches(hasDescendant(withText(superHero.getName()))).check(view, e);
              }
            });
  }

  //Si los super heroes son vengadores se tiene que mostrar la imagen iv_avengers_badge
  @Test public void showsSuperHeroesAvengerIfThereAreSuperHeroes() {
    List<SuperHero> superHeroes = givenThereAreSomeSuperHeroesAvenger(NUMBER_SUPER_HEROES);

    startActivity();

    RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
            .withItems(superHeroes)
            .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
              @Override public void check(SuperHero superHero, View view, NoMatchingViewException e) {
                matches(hasDescendant(allOf(withId(R.id.iv_avengers_badge),withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))).check(view,e);
              }
            });
  }

  //Si los super heroes NO son vengadores se tiene que mostrar la imagen iv_avengers_badge
  @Test public void showsSuperHeroesNOAvengerIfThereAreSuperHeroes() {
    List<SuperHero> superHeroes = givenThereAreSomeSuperHeroes(NUMBER_SUPER_HEROES);

    startActivity();

    RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
            .withItems(superHeroes)
            .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
              @Override public void check(SuperHero superHero, View view, NoMatchingViewException e) {
                matches(hasDescendant(allOf(withId(R.id.iv_avengers_badge),withEffectiveVisibility(ViewMatchers.Visibility.GONE)))).check(view,e);
              }
            });
  }

  private List<SuperHero> givenThereAreSomeSuperHeroes(int superHeroes) {
    return givenThereAreSomeSuperHeroes(superHeroes, false);
  }

  private List<SuperHero> givenThereAreSomeSuperHeroesAvenger(int superHeroes) {
    return givenThereAreSomeSuperHeroes(superHeroes, true);
  }

  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
  private List<SuperHero> givenThereAreSomeSuperHeroes(int numberOfSuperHeroes, boolean isAvenger) {
    List<SuperHero> superHeroes = new LinkedList<>();
    for (int i = 0; i < numberOfSuperHeroes; i++) {
      String superHeroName = "SuperHero - " + i;
      String superHeroPhoto = "https://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg";
      String superHeroDescription = "Description Super Hero - " + i;
      SuperHero superHero =
              new SuperHero(superHeroName, superHeroPhoto, isAvenger,  superHeroDescription);
      superHeroes.add(superHero);
      when(repository.getByName(superHeroName)).thenReturn(superHero);
    }
    when(repository.getAll()).thenReturn(superHeroes);
    return superHeroes;
  }
}