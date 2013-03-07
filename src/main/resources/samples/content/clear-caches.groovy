import sonia.scm.*;
import sonia.scm.cache.*;
import sonia.scm.repository.*;
import sonia.scm.repository.api.*;

// identify your repository
def type = "hg";
def name = "scm-script-plugin";

def repositoryManager = injector.getInstance(RepositoryManager.class);
def repository = repositoryManager.get(type, name);

println "clear caches for " + repository;

def cacheManager = injector.getInstance(CacheManager.class);
def caches = [
  cacheManager.getCache(BlameCommandBuilder.CacheKey.class, BlameResult.class, BlameCommandBuilder.CACHE_NAME),
  cacheManager.getCache(BrowseCommandBuilder.CacheKey.class, BrowserResult.class, BrowseCommandBuilder.CACHE_NAME),
  cacheManager.getCache(LogCommandBuilder.CacheKey.class, ChangesetPagingResult.class, LogCommandBuilder.CACHE_NAME),
  cacheManager.getCache(TagsCommandBuilder.CacheKey.class, Tags.class, TagsCommandBuilder.CACHE_NAME),
  cacheManager.getCache(BranchesCommandBuilder.CacheKey.class, Branches.class, BranchesCommandBuilder.CACHE_NAME)
];

def filter = new RepositoryFilter(repository);
for (def cache : caches){
  cache.removeAll(filter);
}
