/* *
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


Ext.ns('Sonia.panel');

Sonia.panel.Console = Ext.extend(Ext.Panel, {
  
  editorPanel: null,
  outputPanel: null,
  
  initComponent: function(){
    this.editorPanel = new Sonia.panel.CodeEditorPanel({
      region: 'center',
      layout: 'fit'
    });
    
    this.outputPanel = new Ext.Panel({
      region: 'south',
      layout: 'fit',
      height: 250,
      padding: 5,
      split: true,
      autoScroll: true,
      bodyCssClass: 'x-panel-mc'
    });
    
    var config = {
      title: 'Console',
      layout: 'border',
      tbar: [{
        xtype: 'tbbutton',
        text: 'Execute',
        handler: this.execute,
        scope: this
      }],
      items: [this.editorPanel, this.outputPanel]
    };
    
    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.panel.Console.superclass.initComponent.apply(this, arguments);
  },
  
  execute: function(){
    var el = this.el;
    var tid = setTimeout( function(){el.mask('Loading ...');}, 100);
    
    Ext.Ajax.request({
      url: restUrl + 'plugins/script',
      method: 'POST',
      params: this.editorPanel.getValue(),
      scope: this,
      headers: {
        'Content-Type': 'application/x-groovy'
      },
      success: function(response){
        this.outputPanel.update('<pre>' + response.responseText + '</pre>');
        clearTimeout(tid);
        el.unmask();
      },
      failure: function(response){
        this.outputPanel.update('<pre>' + response.responseText + '</pre>');
        clearTimeout(tid);
        el.unmask();
        main.handleFailure(
          response.status, 
          this.errorTitleText, 
          this.errorArchiveMsgText
        );
      }
    });
  }
  
});

Ext.reg('console', Sonia.panel.Console);