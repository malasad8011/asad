const V='asad-v2',FILES=['./','./index.html','./manifest.json'];
self.addEventListener('install',e=>{e.waitUntil(caches.open(V).then(c=>c.addAll(FILES)).then(()=>self.skipWaiting()));});
self.addEventListener('activate',e=>{e.waitUntil(caches.keys().then(k=>Promise.all(k.filter(x=>x!==V).map(x=>caches.delete(x)))).then(()=>self.clients.claim()));});
self.addEventListener('fetch',e=>{const r=e.request;if(r.method!=='GET')return;const u=new URL(r.url);
 if(u.origin!==location.origin){e.respondWith(caches.open(V).then(c=>c.match(r).then(m=>m||fetch(r).then(x=>{c.put(r,x.clone());return x;}).catch(()=>m))));return;}
 e.respondWith(fetch(r).then(x=>{const y=x.clone();caches.open(V).then(c=>c.put(r,y));return x;}).catch(()=>caches.match(r).then(m=>m||caches.match('./index.html'))));});
self.addEventListener('notificationclick',e=>{e.notification.close();const id=(e.notification.data||{}).taskId;e.waitUntil(self.clients.matchAll({type:'window',includeUncontrolled:true}).then(l=>{for(const c of l){if('focus' in c){c.focus();if(id)c.postMessage({taskId:id});return}}return self.clients.openWindow('./'+(id?'#t='+id:''))}));});
