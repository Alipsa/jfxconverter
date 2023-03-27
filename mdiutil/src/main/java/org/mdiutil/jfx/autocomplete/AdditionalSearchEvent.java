/*
Copyright (c) 2019, Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://sourceforge.net/projects/mdiutilities/
 */
package org.mdiutil.jfx.autocomplete;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Represents an additional search Event.
 *
 * @since 0.9.52
 */
public class AdditionalSearchEvent extends Event {
   public static final EventType<AdditionalSearchEvent> FULLTEXTSEARCH = new EventType<>(Event.ANY, "FULLTEXTSEARCH");
   private final String text;

   /**
    * Constructor.
    *
    * @param text the text to use for the full text search
    */
   public AdditionalSearchEvent(String text) {
      super(FULLTEXTSEARCH);
      this.text = text;
   }

   public AdditionalSearchEvent(Object source, EventTarget target, String text) {
      super(source, target, FULLTEXTSEARCH);
      this.text = text;
   }

   @Override
   public AdditionalSearchEvent copyFor(Object newSource, EventTarget newTarget) {
      return (AdditionalSearchEvent) super.copyFor(newSource, newTarget);
   }

   @Override
   public EventType<? extends AdditionalSearchEvent> getEventType() {
      return (EventType<? extends AdditionalSearchEvent>) super.getEventType();
   }

   /**
    * Return the text to use for the full text search.
    *
    * @return the text
    */
   public String getText() {
      return text;
   }

}
