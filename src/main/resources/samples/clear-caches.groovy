/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */
import sonia.scm.*;
import sonia.scm.cache.*;
import sonia.scm.repository.*;
import sonia.scm.repository.api.*;

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