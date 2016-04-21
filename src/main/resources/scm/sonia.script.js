/*
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

Ext.ns('Sonia.script');

Sonia.script.createTypeStore = function(){
  return new Sonia.rest.JsonStore({
    proxy: new Ext.data.HttpProxy({
      url: restUrl + 'plugins/script/types.json',
      method: 'GET'
    }),
    fields: ['name', 'display-name', 'mime-type'],
    root: 'types',
    idProperty: 'name',
    autoLoad: true,
    autoDestroy: true
  });
};

loginCallbacks.push(function(){
  if (admin){
    var navPanel = Ext.getCmp('navigationPanel');
    var count = navPanel.count() - 1;
    navPanel.insertSection(count, {
      title: 'Development',
      links: [{
        label: 'Scripts',
        fn: function(){
          main.addTabPanel('scriptGrid', 'scriptGrid', 'Scripts');
        }          
      },{
        label: 'Script Console',
        fn: function(){
          main.addTabPanel('console', 'console', 'Script Console');
        }
      }]
    });
  }
});