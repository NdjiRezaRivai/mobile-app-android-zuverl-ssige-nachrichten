package com.nachrichten.assessment.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nachrichten.assessment.di.AppContainer
import com.nachrichten.assessment.presentation.articles.ArticlesScreen
import com.nachrichten.assessment.presentation.articles.ArticlesViewModel
import com.nachrichten.assessment.presentation.categories.CategoriesScreen
import com.nachrichten.assessment.presentation.categories.CategoriesViewModel
import com.nachrichten.assessment.presentation.detail.ArticleDetailScreen
import com.nachrichten.assessment.presentation.sources.SourcesScreen
import com.nachrichten.assessment.presentation.sources.SourcesViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController,
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Categories.route,
        modifier = modifier
    ) {
        composable(route = Screen.Categories.route) {
            val viewModel: CategoriesViewModel = viewModel(
                factory = CategoriesViewModel.Factory(appContainer.getCategoriesUseCase)
            )
            CategoriesScreen(
                viewModel = viewModel,
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.Sources.createRoute(categoryId))
                }
            )
        }

        composable(
            route = Screen.Sources.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val viewModel: SourcesViewModel = viewModel(
                factory = SourcesViewModel.Factory(
                    category = category,
                    getSourcesUseCase = appContainer.getSourcesUseCase,
                    searchSourcesUseCase = appContainer.searchSourcesUseCase
                )
            )
            SourcesScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSourceClick = { sourceId, sourceName ->
                    navController.navigate(Screen.Articles.createRoute(sourceId, sourceName))
                }
            )
        }

        composable(
            route = Screen.Articles.route,
            arguments = listOf(
                navArgument("sourceId") { type = NavType.StringType },
                navArgument("sourceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sourceId = backStackEntry.arguments?.getString("sourceId") ?: ""
            val rawSourceName = backStackEntry.arguments?.getString("sourceName") ?: ""
            val sourceName = URLDecoder.decode(rawSourceName, StandardCharsets.UTF_8.toString())

            val viewModel: ArticlesViewModel = viewModel(
                factory = ArticlesViewModel.Factory(
                    sourceId = sourceId,
                    sourceName = sourceName,
                    getArticlesUseCase = appContainer.getArticlesUseCase,
                    searchArticlesUseCase = appContainer.searchArticlesUseCase
                )
            )
            ArticlesScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onArticleClick = { article ->
                    navController.navigate(Screen.ArticleDetail.createRoute(article.url, article.title))
                }
            )
        }

        composable(
            route = Screen.ArticleDetail.route,
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val rawUrl = backStackEntry.arguments?.getString("url") ?: ""
            val rawTitle = backStackEntry.arguments?.getString("title") ?: ""
            val url = URLDecoder.decode(rawUrl, StandardCharsets.UTF_8.toString())
            val title = URLDecoder.decode(rawTitle, StandardCharsets.UTF_8.toString())

            ArticleDetailScreen(
                articleUrl = url,
                articleTitle = title,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
