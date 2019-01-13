package modules

import com.google.inject.Inject


class ApplicationStart @Inject()(options: dao.Options, blogrolls: dao.Blogroll) {
  options.load()
  blogrolls.load()
}
